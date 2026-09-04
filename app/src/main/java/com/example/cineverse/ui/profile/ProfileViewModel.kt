package com.example.cineverse.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cineverse.domain.model.Profile
import com.example.cineverse.domain.repository.ProfileRepository
import com.example.cineverse.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Why a candidate name/bio pair can't be saved, so the form can point at the offending field. */
sealed class ProfileFormError {
    data object BlankName : ProfileFormError()
    data object NameTooLong : ProfileFormError()
    data object BioTooLong : ProfileFormError()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    val uiState: StateFlow<Resource<Profile>> = repository.observeProfile()
        .map<Profile, Resource<Profile>> { Resource.Success(it) }
        .catch { e -> emit(Resource.Error(e.message ?: "Couldn't load profile.", e)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Resource.Loading)

    /** Pure so the edit form can validate on every keystroke without touching Room. */
    fun validate(name: String, bio: String): ProfileFormError? = when {
        name.isBlank() -> ProfileFormError.BlankName
        name.trim().length > MAX_NAME_LENGTH -> ProfileFormError.NameTooLong
        bio.trim().length > MAX_BIO_LENGTH -> ProfileFormError.BioTooLong
        else -> null
    }

    /** Returns false without saving if [name]/[bio] fail [validate]. */
    fun save(name: String, bio: String): Boolean {
        if (validate(name, bio) != null) return false
        viewModelScope.launch {
            repository.saveProfile(Profile(displayName = name.trim(), bio = bio.trim()))
        }
        return true
    }

    companion object {
        const val MAX_NAME_LENGTH = 50
        const val MAX_BIO_LENGTH = 200
    }
}

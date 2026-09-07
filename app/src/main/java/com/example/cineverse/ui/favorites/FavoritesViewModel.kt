package com.example.cineverse.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cineverse.domain.model.Movie
import com.example.cineverse.domain.repository.MovieRepository
import com.example.cineverse.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    val uiState: StateFlow<Resource<List<Movie>>> = repository.observeFavorites()
        .map<List<Movie>, Resource<List<Movie>>> { Resource.Success(it) }
        .catch { e -> emit(Resource.Error(e.message ?: "Couldn't load favorites.", e)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Resource.Loading)

    private val _pendingRemoval = MutableStateFlow<Movie?>(null)
    /** The movie a swipe-to-delete gesture is asking to confirm removal for, if any. */
    val pendingRemoval: StateFlow<Movie?> = _pendingRemoval

    fun requestRemoval(movie: Movie) {
        _pendingRemoval.value = movie
    }

    fun confirmPendingRemoval() {
        val movie = _pendingRemoval.value ?: return
        _pendingRemoval.value = null
        viewModelScope.launch { repository.removeFavorite(movie.id) }
    }

    fun cancelPendingRemoval() {
        _pendingRemoval.value = null
    }

    fun clearAll() {
        viewModelScope.launch { repository.clearFavorites() }
    }
}

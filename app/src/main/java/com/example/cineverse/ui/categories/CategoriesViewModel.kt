package com.example.cineverse.ui.categories

import androidx.lifecycle.ViewModel
import com.example.cineverse.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/** Skeleton only — will expose the list of movie categories shown as tabs. */
@HiltViewModel
class CategoriesViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<Unit>>(Resource.Loading)
    val uiState: StateFlow<Resource<Unit>> = _uiState.asStateFlow()
}

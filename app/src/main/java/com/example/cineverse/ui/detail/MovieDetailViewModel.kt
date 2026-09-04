package com.example.cineverse.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cineverse.domain.model.Movie
import com.example.cineverse.domain.repository.MovieRepository
import com.example.cineverse.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: MovieRepository
) : ViewModel() {

    private val movieId: Int = checkNotNull(savedStateHandle["movieId"])

    private val _uiState = MutableStateFlow<Resource<Movie>>(Resource.Loading)
    val uiState: StateFlow<Resource<Movie>> = _uiState.asStateFlow()

    init {
        loadMovie()
    }

    fun retry() = loadMovie()

    private fun loadMovie() {
        viewModelScope.launch {
            _uiState.value = Resource.Loading
            _uiState.value = repository.getMovieDetail(movieId)
        }
    }
}

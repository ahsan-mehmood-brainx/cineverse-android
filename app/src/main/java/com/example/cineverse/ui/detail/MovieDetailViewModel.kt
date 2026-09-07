package com.example.cineverse.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cineverse.domain.model.Movie
import com.example.cineverse.domain.repository.MovieRepository
import com.example.cineverse.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
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

    val isFavorite: StateFlow<Boolean> = repository.observeIsFavorite(movieId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        loadMovie()
    }

    fun retry() = loadMovie()

    fun toggleFavorite() {
        val movie = (_uiState.value as? Resource.Success)?.data ?: return
        viewModelScope.launch {
            if (isFavorite.value) {
                repository.removeFavorite(movie.id)
            } else {
                repository.addFavorite(movie)
            }
        }
    }

    private fun loadMovie() {
        viewModelScope.launch {
            _uiState.value = Resource.Loading
            _uiState.value = repository.getMovieDetail(movieId)
        }
    }
}

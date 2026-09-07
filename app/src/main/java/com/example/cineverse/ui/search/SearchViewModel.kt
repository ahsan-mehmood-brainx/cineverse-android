package com.example.cineverse.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cineverse.domain.model.Movie
import com.example.cineverse.domain.repository.MovieRepository
import com.example.cineverse.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<List<Movie>>>(Resource.Success(emptyList()))
    val uiState: StateFlow<Resource<List<Movie>>> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var lastQuery: String = ""

    fun onQueryChanged(query: String) {
        val trimmed = query.trim()
        lastQuery = trimmed
        searchJob?.cancel()

        if (trimmed.isEmpty()) {
            _uiState.value = Resource.Success(emptyList())
            return
        }

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            _uiState.value = Resource.Loading
            _uiState.value = repository.searchMovies(trimmed)
        }
    }

    fun retry() = onQueryChanged(lastQuery)

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 400L
    }
}

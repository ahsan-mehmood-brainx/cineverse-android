package com.example.cineverse.ui.categories

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

/**
 * Fetches the movie list for a single [MovieCategory]. One instance backs each ViewPager2 page,
 * so [load] is called once per page and is a no-op on later calls (e.g. after a config change)
 * once that page's category has already been loaded.
 */
@HiltViewModel
class CategoryMoviesViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<List<Movie>>>(Resource.Loading)
    val uiState: StateFlow<Resource<List<Movie>>> = _uiState.asStateFlow()

    private var loadedCategory: MovieCategory? = null

    fun load(category: MovieCategory) {
        if (loadedCategory == category) return
        loadedCategory = category
        fetch(category)
    }

    fun retry() {
        loadedCategory?.let { fetch(it) }
    }

    private fun fetch(category: MovieCategory) {
        viewModelScope.launch {
            _uiState.value = Resource.Loading
            _uiState.value = when (category) {
                MovieCategory.POPULAR -> repository.getPopularMovies()
                MovieCategory.TOP_RATED -> repository.getTopRatedMovies()
                MovieCategory.UPCOMING -> repository.getUpcomingMovies()
                MovieCategory.NOW_PLAYING -> repository.getNowPlayingMovies()
            }
        }
    }
}

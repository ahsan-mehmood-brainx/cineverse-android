package com.example.cineverse.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cineverse.domain.repository.MovieRepository
import com.example.cineverse.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<HomeUiState>>(Resource.Loading)
    val uiState: StateFlow<Resource<HomeUiState>> = _uiState.asStateFlow()

    init {
        loadHome()
    }

    fun retry() = loadHome()

    private fun loadHome() {
        viewModelScope.launch {
            _uiState.value = Resource.Loading

            val trendingDeferred = async { repository.getTrendingMovies() }
            val popularDeferred = async { repository.getPopularMovies() }
            val topRatedDeferred = async { repository.getTopRatedMovies() }
            val genresDeferred = async { repository.getGenres() }

            val trending = trendingDeferred.await()
            val popular = popularDeferred.await()
            val topRated = topRatedDeferred.await()
            val genres = genresDeferred.await()

            _uiState.value = when {
                trending is Resource.Error -> trending
                popular is Resource.Error -> popular
                topRated is Resource.Error -> topRated
                genres is Resource.Error -> genres
                trending is Resource.Success && popular is Resource.Success &&
                    topRated is Resource.Success && genres is Resource.Success ->
                    Resource.Success(
                        HomeUiState(
                            trendingMovies = trending.data,
                            popularMovies = popular.data,
                            topRatedMovies = topRated.data,
                            genres = genres.data
                        )
                    )
                else -> Resource.Error("Unexpected error.")
            }
        }
    }
}

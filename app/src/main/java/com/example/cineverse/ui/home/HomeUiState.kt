package com.example.cineverse.ui.home

import com.example.cineverse.domain.model.Genre
import com.example.cineverse.domain.model.Movie

data class HomeUiState(
    val trendingMovies: List<Movie>,
    val popularMovies: List<Movie>,
    val topRatedMovies: List<Movie>,
    val genres: List<Genre>
) {
    val isEmpty: Boolean
        get() = trendingMovies.isEmpty() && popularMovies.isEmpty() && topRatedMovies.isEmpty()
}

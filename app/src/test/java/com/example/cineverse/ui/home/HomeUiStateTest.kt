package com.example.cineverse.ui.home

import com.example.cineverse.domain.model.Genre
import com.example.cineverse.domain.model.Movie
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HomeUiStateTest {

    private val sampleMovie = Movie(1, "Test Movie", null, 8.0, "2024-01-01", "Action", "Overview")
    private val sampleGenre = Genre(1, "Action")

    @Test
    fun homeUiState_isEmpty_whenAllListsEmpty() {
        val state = HomeUiState(
            trendingMovies = emptyList(),
            popularMovies = emptyList(),
            topRatedMovies = emptyList(),
            genres = emptyList()
        )

        assertThat(state.isEmpty).isTrue()
    }

    @Test
    fun homeUiState_notEmpty_whenTrendingMoviesNotEmpty() {
        val state = HomeUiState(
            trendingMovies = listOf(sampleMovie),
            popularMovies = emptyList(),
            topRatedMovies = emptyList(),
            genres = emptyList()
        )

        assertThat(state.isEmpty).isFalse()
    }

    @Test
    fun homeUiState_notEmpty_whenPopularMoviesNotEmpty() {
        val state = HomeUiState(
            trendingMovies = emptyList(),
            popularMovies = listOf(sampleMovie),
            topRatedMovies = emptyList(),
            genres = emptyList()
        )

        assertThat(state.isEmpty).isFalse()
    }

    @Test
    fun homeUiState_notEmpty_whenTopRatedMoviesNotEmpty() {
        val state = HomeUiState(
            trendingMovies = emptyList(),
            popularMovies = emptyList(),
            topRatedMovies = listOf(sampleMovie),
            genres = emptyList()
        )

        assertThat(state.isEmpty).isFalse()
    }

    @Test
    fun homeUiState_containsAllMovieCategories() {
        val movie = Movie(1, "Test", null, 8.0, "2024-01-01", "Action", "Overview")
        val state = HomeUiState(
            trendingMovies = listOf(movie),
            popularMovies = listOf(movie),
            topRatedMovies = listOf(movie),
            genres = emptyList()
        )

        assertThat(state.trendingMovies).hasSize(1)
        assertThat(state.popularMovies).hasSize(1)
        assertThat(state.topRatedMovies).hasSize(1)
    }

    @Test
    fun homeUiState_containsGenres() {
        val state = HomeUiState(
            trendingMovies = emptyList(),
            popularMovies = emptyList(),
            topRatedMovies = emptyList(),
            genres = listOf(sampleGenre, Genre(2, "Drama"))
        )

        assertThat(state.genres).hasSize(2)
    }

    @Test
    fun homeUiState_equality() {
        val state1 = HomeUiState(
            trendingMovies = listOf(sampleMovie),
            popularMovies = emptyList(),
            topRatedMovies = emptyList(),
            genres = listOf(sampleGenre)
        )
        val state2 = HomeUiState(
            trendingMovies = listOf(sampleMovie),
            popularMovies = emptyList(),
            topRatedMovies = emptyList(),
            genres = listOf(sampleGenre)
        )

        assertThat(state1).isEqualTo(state2)
    }
}

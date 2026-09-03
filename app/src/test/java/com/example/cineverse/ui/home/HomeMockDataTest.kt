package com.example.cineverse.ui.home

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HomeMockDataTest {

    @Test
    fun homeMockData_hasTrendingMovies() {
        assertThat(HomeMockData.trendingMovies).isNotEmpty()
    }

    @Test
    fun homeMockData_trendingMovies_count() {
        assertThat(HomeMockData.trendingMovies).hasSize(5)
    }

    @Test
    fun homeMockData_hasPopularMovies() {
        assertThat(HomeMockData.popularMovies).isNotEmpty()
    }

    @Test
    fun homeMockData_popularMovies_count() {
        assertThat(HomeMockData.popularMovies).hasSize(5)
    }

    @Test
    fun homeMockData_hasTopRatedMovies() {
        assertThat(HomeMockData.topRatedMovies).isNotEmpty()
    }

    @Test
    fun homeMockData_topRatedMovies_count() {
        assertThat(HomeMockData.topRatedMovies).hasSize(5)
    }

    @Test
    fun homeMockData_hasGenres() {
        assertThat(HomeMockData.genres).isNotEmpty()
    }

    @Test
    fun homeMockData_genres_count() {
        assertThat(HomeMockData.genres).hasSize(8)
    }

    @Test
    fun homeMockData_allMoviesHaveValidFields() {
        HomeMockData.trendingMovies.forEach { movie ->
            assertThat(movie.id).isGreaterThan(0)
            assertThat(movie.title).isNotEmpty()
            assertThat(movie.rating).isGreaterThan(0.0)
            assertThat(movie.releaseDate).isNotEmpty()
            assertThat(movie.genre).isNotEmpty()
            assertThat(movie.overview).isNotEmpty()
        }
    }

    @Test
    fun homeMockData_allGenresHaveValidFields() {
        HomeMockData.genres.forEach { genre ->
            assertThat(genre.id).isGreaterThan(0)
            assertThat(genre.name).isNotEmpty()
        }
    }

    @Test
    fun homeMockData_toUiState_convertsSuccessfully() {
        val uiState = HomeMockData.toUiState()

        assertThat(uiState.trendingMovies).hasSize(5)
        assertThat(uiState.popularMovies).hasSize(5)
        assertThat(uiState.topRatedMovies).hasSize(5)
        assertThat(uiState.genres).hasSize(8)
    }

    @Test
    fun homeMockData_toUiState_notEmpty() {
        val uiState = HomeMockData.toUiState()

        assertThat(uiState.isEmpty).isFalse()
    }

    @Test
    fun homeMockData_trendingMovies_containsDarkKnight() {
        val darkKnight = HomeMockData.trendingMovies.find { it.title == "The Dark Knight" }

        assertThat(darkKnight).isNotNull()
        assertThat(darkKnight?.rating).isEqualTo(9.0)
        assertThat(darkKnight?.genre).isEqualTo("Action")
    }

    @Test
    fun homeMockData_genres_containsAllMainGenres() {
        val genreNames = HomeMockData.genres.map { it.name }

        assertThat(genreNames).containsAtLeast("Action", "Drama", "Sci-Fi", "Comedy")
    }

    @Test
    fun homeMockData_posterUrlsAreNull() {
        val allMovies = HomeMockData.trendingMovies +
                       HomeMockData.popularMovies +
                       HomeMockData.topRatedMovies

        allMovies.forEach { movie ->
            assertThat(movie.posterUrl).isNull()
        }
    }
}

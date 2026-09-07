package com.example.cineverse.data.mapper

import com.example.cineverse.data.remote.dto.GenreDto
import com.example.cineverse.data.remote.dto.MovieDetailDto
import com.example.cineverse.data.remote.dto.MovieDto
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MovieMapperTest {

    @Test
    fun movieDto_toDomain_buildsFullPosterUrl() {
        val dto = MovieDto(id = 1, title = "Dune", posterPath = "/abc.jpg")

        val movie = dto.toDomain(emptyMap())

        assertThat(movie.posterUrl).isEqualTo("https://image.tmdb.org/t/p/w500/abc.jpg")
    }

    @Test
    fun movieDto_toDomain_nullPosterPath_mapsToNullUrl() {
        val dto = MovieDto(id = 1, title = "Dune", posterPath = null)

        val movie = dto.toDomain(emptyMap())

        assertThat(movie.posterUrl).isNull()
    }

    @Test
    fun movieDto_toDomain_resolvesGenreIdsToNames() {
        val dto = MovieDto(id = 1, title = "Dune", genreIds = listOf(1, 5))

        val movie = dto.toDomain(mapOf(1 to "Action", 5 to "Sci-Fi"))

        assertThat(movie.genre).isEqualTo("Action, Sci-Fi")
    }

    @Test
    fun movieDto_toDomain_unknownGenreId_isSkipped() {
        val dto = MovieDto(id = 1, title = "Dune", genreIds = listOf(1, 999))

        val movie = dto.toDomain(mapOf(1 to "Action"))

        assertThat(movie.genre).isEqualTo("Action")
    }

    @Test
    fun movieDto_toDomain_missingReleaseDate_mapsToEmptyString() {
        val dto = MovieDto(id = 1, title = "Dune", releaseDate = null)

        val movie = dto.toDomain(emptyMap())

        assertThat(movie.releaseDate).isEmpty()
    }

    @Test
    fun movieDetailDto_toDomain_joinsGenreNames() {
        val dto = MovieDetailDto(
            id = 1,
            title = "Dune",
            posterPath = "/abc.jpg",
            voteAverage = 8.3,
            releaseDate = "2021-10-22",
            overview = "A noble heir.",
            genres = listOf(GenreDto(1, "Action"), GenreDto(5, "Sci-Fi"))
        )

        val movie = dto.toDomain()

        assertThat(movie.genre).isEqualTo("Action, Sci-Fi")
        assertThat(movie.posterUrl).isEqualTo("https://image.tmdb.org/t/p/w500/abc.jpg")
        assertThat(movie.rating).isEqualTo(8.3)
        assertThat(movie.overview).isEqualTo("A noble heir.")
    }
}

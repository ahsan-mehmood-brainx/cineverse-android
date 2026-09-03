package com.example.cineverse.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MovieTest {

    @Test
    fun movie_createsMovieWithAllFields() {
        val movie = Movie(
            id = 1,
            title = "The Dark Knight",
            posterUrl = "https://example.com/poster.jpg",
            rating = 9.0,
            releaseDate = "2008-07-18",
            genre = "Action",
            overview = "Batman raises the stakes battling the Joker's reign of chaos over Gotham."
        )

        assertThat(movie.id).isEqualTo(1)
        assertThat(movie.title).isEqualTo("The Dark Knight")
        assertThat(movie.posterUrl).isEqualTo("https://example.com/poster.jpg")
        assertThat(movie.rating).isEqualTo(9.0)
        assertThat(movie.releaseDate).isEqualTo("2008-07-18")
        assertThat(movie.genre).isEqualTo("Action")
        assertThat(movie.overview).contains("Batman")
    }

    @Test
    fun movie_withNullPosterUrl() {
        val movie = Movie(
            id = 2,
            title = "Inception",
            posterUrl = null,
            rating = 8.8,
            releaseDate = "2010-07-16",
            genre = "Sci-Fi",
            overview = "A thief who steals corporate secrets through dream-sharing is given a chance at redemption."
        )

        assertThat(movie.posterUrl).isNull()
    }

    @Test
    fun movie_equality() {
        val movie1 = Movie(1, "The Dark Knight", null, 9.0, "2008-07-18", "Action", "Overview")
        val movie2 = Movie(1, "The Dark Knight", null, 9.0, "2008-07-18", "Action", "Overview")

        assertThat(movie1).isEqualTo(movie2)
    }

    @Test
    fun movie_inequality_differentId() {
        val movie1 = Movie(1, "The Dark Knight", null, 9.0, "2008-07-18", "Action", "Overview")
        val movie2 = Movie(2, "The Dark Knight", null, 9.0, "2008-07-18", "Action", "Overview")

        assertThat(movie1).isNotEqualTo(movie2)
    }

    @Test
    fun movie_inequality_differentTitle() {
        val movie1 = Movie(1, "The Dark Knight", null, 9.0, "2008-07-18", "Action", "Overview")
        val movie2 = Movie(1, "Inception", null, 9.0, "2008-07-18", "Action", "Overview")

        assertThat(movie1).isNotEqualTo(movie2)
    }

    @Test
    fun movie_inequality_differentRating() {
        val movie1 = Movie(1, "The Dark Knight", null, 9.0, "2008-07-18", "Action", "Overview")
        val movie2 = Movie(1, "The Dark Knight", null, 8.5, "2008-07-18", "Action", "Overview")

        assertThat(movie1).isNotEqualTo(movie2)
    }
}

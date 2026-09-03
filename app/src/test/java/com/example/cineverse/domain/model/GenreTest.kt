package com.example.cineverse.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GenreTest {

    @Test
    fun genre_createsGenreWithAllFields() {
        val genre = Genre(id = 1, name = "Action")

        assertThat(genre.id).isEqualTo(1)
        assertThat(genre.name).isEqualTo("Action")
    }

    @Test
    fun genre_equality() {
        val genre1 = Genre(1, "Action")
        val genre2 = Genre(1, "Action")

        assertThat(genre1).isEqualTo(genre2)
    }

    @Test
    fun genre_inequality_differentId() {
        val genre1 = Genre(1, "Action")
        val genre2 = Genre(2, "Action")

        assertThat(genre1).isNotEqualTo(genre2)
    }

    @Test
    fun genre_inequality_differentName() {
        val genre1 = Genre(1, "Action")
        val genre2 = Genre(1, "Comedy")

        assertThat(genre1).isNotEqualTo(genre2)
    }
}

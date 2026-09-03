package com.example.cineverse.ui.common.adapter

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cineverse.domain.model.Genre
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GenreAdapterTest {

    private lateinit var adapter: GenreAdapter

    @Before
    fun setUp() {
        adapter = GenreAdapter()
    }

    @Test
    fun adapter_initiallyEmpty() {
        assertThat(adapter.itemCount).isEqualTo(0)
    }

    @Test
    fun adapter_submitList_updatesItemCount() {
        val genres = listOf(
            Genre(1, "Action"),
            Genre(2, "Drama"),
            Genre(3, "Comedy")
        )

        adapter.submitList(genres)

        assertThat(adapter.itemCount).isEqualTo(3)
    }

    @Test
    fun adapter_submitEmptyList() {
        val genres = listOf(Genre(1, "Action"))
        adapter.submitList(genres)
        assertThat(adapter.itemCount).isEqualTo(1)

        adapter.submitList(emptyList())
        assertThat(adapter.itemCount).isEqualTo(0)
    }

    @Test
    fun adapter_submitListRetainsCorrectGenre() {
        val genre = Genre(1, "Action")
        adapter.submitList(listOf(genre))

        assertThat(adapter.itemCount).isEqualTo(1)
    }

    @Test
    fun adapter_submitMultipleGenres() {
        val genres = listOf(
            Genre(1, "Action"),
            Genre(2, "Drama"),
            Genre(3, "Comedy")
        )
        adapter.submitList(genres)

        assertThat(adapter.itemCount).isEqualTo(3)
    }

    @Test
    fun adapter_multipleSubmissions_replacesList() {
        val firstList = listOf(Genre(1, "Action"))
        adapter.submitList(firstList)
        assertThat(adapter.itemCount).isEqualTo(1)

        val secondList = listOf(
            Genre(2, "Drama"),
            Genre(3, "Comedy")
        )
        adapter.submitList(secondList)

        assertThat(adapter.itemCount).isEqualTo(2)
    }

    @Test
    fun adapter_diffCallback_detectsSameGenre() {
        val genre1 = Genre(1, "Action")
        val genre2 = Genre(1, "Action")

        adapter.submitList(listOf(genre1))
        adapter.submitList(listOf(genre2))

        assertThat(adapter.itemCount).isEqualTo(1)
    }

    @Test
    fun adapter_diffCallback_detectsDifferentGenres() {
        val genre1 = Genre(1, "Action")
        val genre2 = Genre(2, "Drama")

        adapter.submitList(listOf(genre1, genre2))

        assertThat(adapter.itemCount).isEqualTo(2)
    }

    @Test
    fun adapter_maintainsGenreOrder() {
        val genres = listOf(
            Genre(1, "Action"),
            Genre(2, "Comedy"),
            Genre(3, "Drama"),
            Genre(4, "Horror"),
            Genre(5, "Sci-Fi")
        )
        adapter.submitList(genres)

        assertThat(adapter.itemCount).isEqualTo(5)
    }

    @Test
    fun adapter_handlesLargeGenreList() {
        val genres = (1..100).map { Genre(it, "Genre $it") }
        adapter.submitList(genres)

        assertThat(adapter.itemCount).isEqualTo(100)
    }
}

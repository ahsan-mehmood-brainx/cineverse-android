package com.example.cineverse.ui.common.adapter

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cineverse.domain.model.Movie
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieAdapterTest {

    private lateinit var adapter: MovieAdapter
    private var movieClickedId: Int? = null

    @Before
    fun setUp() {
        adapter = MovieAdapter(onMovieClick = { movie ->
            movieClickedId = movie.id
        })
    }

    @Test
    fun adapter_initiallyEmpty() {
        assertThat(adapter.itemCount).isEqualTo(0)
    }

    @Test
    fun adapter_submitList_updatesItemCount() {
        val movies = listOf(
            Movie(1, "Movie 1", null, 8.0, "2024-01-01", "Action", "Overview 1"),
            Movie(2, "Movie 2", null, 8.5, "2024-01-02", "Drama", "Overview 2")
        )

        adapter.submitList(movies)

        assertThat(adapter.itemCount).isEqualTo(2)
    }

    @Test
    fun adapter_submitEmptyList() {
        val movies = listOf(Movie(1, "Movie 1", null, 8.0, "2024-01-01", "Action", "Overview"))
        adapter.submitList(movies)
        assertThat(adapter.itemCount).isEqualTo(1)

        adapter.submitList(emptyList())
        assertThat(adapter.itemCount).isEqualTo(0)
    }

    @Test
    fun adapter_multipleSubmissions_replacesList() {
        val firstList = listOf(Movie(1, "Movie 1", null, 8.0, "2024-01-01", "Action", "Overview"))
        adapter.submitList(firstList)
        assertThat(adapter.itemCount).isEqualTo(1)

        val secondList = listOf(
            Movie(2, "Movie 2", null, 8.5, "2024-01-02", "Drama", "Overview"),
            Movie(3, "Movie 3", null, 8.3, "2024-01-03", "Thriller", "Overview")
        )
        adapter.submitList(secondList)

        assertThat(adapter.itemCount).isEqualTo(2)
    }

    @Test
    fun adapter_diffCallback_detectsSameItem() {
        val movie1 = Movie(1, "Movie 1", null, 8.0, "2024-01-01", "Action", "Overview")
        val movie2 = Movie(1, "Movie 1", null, 8.0, "2024-01-01", "Action", "Overview")

        adapter.submitList(listOf(movie1))
        adapter.submitList(listOf(movie2))

        assertThat(adapter.itemCount).isEqualTo(1)
    }

    @Test
    fun adapter_diffCallback_detectsDifferentItems() {
        val movie1 = Movie(1, "Movie 1", null, 8.0, "2024-01-01", "Action", "Overview")
        val movie2 = Movie(2, "Movie 2", null, 8.0, "2024-01-01", "Action", "Overview")

        adapter.submitList(listOf(movie1, movie2))

        assertThat(adapter.itemCount).isEqualTo(2)
    }

    @Test
    fun adapter_handlesNullPosterUrl_inSubmission() {
        val movie = Movie(1, "Movie 1", null, 8.0, "2024-01-01", "Action", "Overview")
        adapter.submitList(listOf(movie))

        assertThat(adapter.itemCount).isEqualTo(1)
    }

    @Test
    fun adapter_handlesValidPosterUrl_inSubmission() {
        val movie = Movie(1, "Movie 1", "https://example.com/poster.jpg", 8.0, "2024-01-01", "Action", "Overview")
        adapter.submitList(listOf(movie))

        assertThat(adapter.itemCount).isEqualTo(1)
    }

    @Test
    fun adapter_largeMovieList() {
        val movies = (1..50).map {
            Movie(it, "Movie $it", null, 8.0 + it, "2024-01-01", "Action", "Overview")
        }
        adapter.submitList(movies)

        assertThat(adapter.itemCount).isEqualTo(50)
    }
}

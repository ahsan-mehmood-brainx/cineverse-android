package com.example.cineverse.ui.favorites

import app.cash.turbine.turbineScope
import com.example.cineverse.domain.model.Genre
import com.example.cineverse.domain.model.Movie
import com.example.cineverse.domain.repository.MovieRepository
import com.example.cineverse.util.Resource
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val movie = Movie(1, "Dune", null, 8.3, "2021-10-22", "Sci-Fi", "A noble heir.")

    private class FakeMovieRepository : MovieRepository {
        val favorites = MutableStateFlow<List<Movie>>(emptyList())
        var removedId: Int? = null

        override suspend fun getTrendingMovies() = throw NotImplementedError()
        override suspend fun getPopularMovies() = throw NotImplementedError()
        override suspend fun getTopRatedMovies() = throw NotImplementedError()
        override suspend fun getNowPlayingMovies() = throw NotImplementedError()
        override suspend fun getUpcomingMovies() = throw NotImplementedError()
        override suspend fun getGenres() = Resource.Success(emptyList<Genre>())
        override suspend fun searchMovies(query: String) = throw NotImplementedError()
        override suspend fun getMovieDetail(movieId: Int) = throw NotImplementedError()

        override fun observeFavorites() = favorites
        override fun observeIsFavorite(movieId: Int) = throw NotImplementedError()
        override suspend fun addFavorite(movie: Movie) = throw NotImplementedError()
        override suspend fun removeFavorite(movieId: Int) {
            removedId = movieId
            favorites.value = favorites.value.filterNot { it.id == movieId }
        }

        var clearedAll = false
        override suspend fun clearFavorites() {
            clearedAll = true
            favorites.value = emptyList()
        }
    }

    private lateinit var repository: FakeMovieRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeMovieRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_reflectsFavoritesFromRepository() = runTest(testDispatcher) {
        turbineScope {
            repository.favorites.value = listOf(movie)
            val viewModel = FavoritesViewModel(repository)
            val uiStateTurbine = viewModel.uiState.testIn(this)
            advanceUntilIdle()

            uiStateTurbine.awaitItem() // Loading
            val success = uiStateTurbine.awaitItem()
            assertThat(success).isInstanceOf(Resource.Success::class.java)
            assertThat((success as Resource.Success).data).containsExactly(movie)

            uiStateTurbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun confirmPendingRemoval_removesRequestedMovieAndClearsPendingState() = runTest(testDispatcher) {
        repository.favorites.value = listOf(movie)
        val viewModel = FavoritesViewModel(repository)
        advanceUntilIdle()

        viewModel.requestRemoval(movie)
        assertThat(viewModel.pendingRemoval.value).isEqualTo(movie)

        viewModel.confirmPendingRemoval()
        advanceUntilIdle()

        assertThat(viewModel.pendingRemoval.value).isNull()
        assertThat(repository.removedId).isEqualTo(movie.id)
    }

    @Test
    fun cancelPendingRemoval_doesNotRemoveMovie() = runTest(testDispatcher) {
        repository.favorites.value = listOf(movie)
        val viewModel = FavoritesViewModel(repository)
        advanceUntilIdle()

        viewModel.requestRemoval(movie)
        viewModel.cancelPendingRemoval()
        advanceUntilIdle()

        assertThat(viewModel.pendingRemoval.value).isNull()
        assertThat(repository.removedId).isNull()
    }

    @Test
    fun clearAll_removesAllFavorites() = runTest(testDispatcher) {
        repository.favorites.value = listOf(movie)
        val viewModel = FavoritesViewModel(repository)
        advanceUntilIdle()

        viewModel.clearAll()
        advanceUntilIdle()

        assertThat(repository.clearedAll).isTrue()
        assertThat(repository.favorites.value).isEmpty()
    }
}

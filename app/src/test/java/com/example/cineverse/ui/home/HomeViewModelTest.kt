package com.example.cineverse.ui.home

import app.cash.turbine.turbineScope
import com.example.cineverse.domain.model.Genre
import com.example.cineverse.domain.model.Movie
import com.example.cineverse.domain.repository.MovieRepository
import com.example.cineverse.util.Resource
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val trendingMovies = listOf(
        Movie(1, "The Dark Knight", null, 9.0, "2008-07-18", "Action", "Batman raises the stakes."),
        Movie(2, "Inception", null, 8.8, "2010-07-16", "Sci-Fi", "A thief who steals corporate secrets."),
        Movie(3, "Interstellar", null, 8.7, "2014-11-07", "Sci-Fi", "A team of explorers."),
        Movie(4, "Dune", null, 8.3, "2021-10-22", "Sci-Fi", "A noble heir."),
        Movie(5, "Oppenheimer", null, 8.6, "2023-07-21", "Drama", "The story of the physicist.")
    )
    private val popularMovies = List(5) { Movie(it + 6, "Popular ${it + 1}", null, 7.9, "2020-01-01", "Action", "Overview") }
    private val topRatedMovies = List(5) { Movie(it + 11, "Top Rated ${it + 1}", null, 9.1, "1994-01-01", "Drama", "Overview") }
    private val genres = List(8) { Genre(it + 1, "Genre ${it + 1}") }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun fakeRepository(
        trending: Resource<List<Movie>> = Resource.Success(trendingMovies),
        popular: Resource<List<Movie>> = Resource.Success(popularMovies),
        topRated: Resource<List<Movie>> = Resource.Success(topRatedMovies),
        genreList: Resource<List<Genre>> = Resource.Success(genres)
    ): MovieRepository = object : MovieRepository {
        override suspend fun getTrendingMovies() = trending
        override suspend fun getPopularMovies() = popular
        override suspend fun getTopRatedMovies() = topRated
        override suspend fun getNowPlayingMovies() = popular
        override suspend fun getUpcomingMovies() = popular
        override suspend fun getGenres() = genreList
        override suspend fun searchMovies(query: String) = Resource.Success(emptyList<Movie>())
        override suspend fun getMovieDetail(movieId: Int) = Resource.Success(trendingMovies.first())
    }

    @Test
    fun viewModel_initializesWithLoading() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(fakeRepository())

        assertThat(viewModel.uiState.value).isInstanceOf(Resource.Loading::class.java)
    }

    @Test
    fun viewModel_emitsSuccessState() = runTest(testDispatcher) {
        turbineScope {
            val viewModel = HomeViewModel(fakeRepository())
            val uiStateTurbine = viewModel.uiState.testIn(this)
            advanceUntilIdle()

            val loadingState = uiStateTurbine.awaitItem()
            assertThat(loadingState).isInstanceOf(Resource.Loading::class.java)

            val successState = uiStateTurbine.awaitItem()
            assertThat(successState).isInstanceOf(Resource.Success::class.java)

            val data = (successState as Resource.Success).data
            assertThat(data.trendingMovies).hasSize(5)
            assertThat(data.popularMovies).hasSize(5)
            assertThat(data.topRatedMovies).hasSize(5)
            assertThat(data.genres).hasSize(8)

            uiStateTurbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun viewModel_successStateHasNonEmptyData() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(fakeRepository())
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(Resource.Success::class.java)

        val uiState = (state as Resource.Success).data
        assertThat(uiState.isEmpty).isFalse()
    }

    @Test
    fun viewModel_retry_reloadsData() = runTest(testDispatcher) {
        turbineScope {
            val viewModel = HomeViewModel(fakeRepository())
            val uiStateTurbine = viewModel.uiState.testIn(this)
            advanceUntilIdle()

            uiStateTurbine.awaitItem() // Loading
            uiStateTurbine.awaitItem() // Success

            viewModel.retry()
            advanceUntilIdle()

            uiStateTurbine.awaitItem() // Loading again
            val successState = uiStateTurbine.awaitItem()
            assertThat(successState).isInstanceOf(Resource.Success::class.java)

            uiStateTurbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun viewModel_emitsError_whenRepositoryFails() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(fakeRepository(trending = Resource.Error("network down")))
        advanceUntilIdle()

        assertThat(viewModel.uiState.value).isInstanceOf(Resource.Error::class.java)
    }

    @Test
    fun viewModel_loadedDataContainsExpectedMovies() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(fakeRepository())
        advanceUntilIdle()

        val uiState = (viewModel.uiState.value as? Resource.Success)?.data

        assertThat(uiState).isNotNull()
        uiState?.let { state ->
            assertThat(state.trendingMovies.map { it.title })
                .containsAtLeast("The Dark Knight", "Inception", "Interstellar")
        }
    }
}

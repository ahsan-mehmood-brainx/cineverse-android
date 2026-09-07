package com.example.cineverse.ui.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.turbineScope
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
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val movie = Movie(123, "Dune", null, 8.3, "2021-10-22", "Sci-Fi", "A noble heir.")

    private lateinit var repository: MovieRepository
    private lateinit var isFavoriteFlow: MutableStateFlow<Boolean>

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        isFavoriteFlow = MutableStateFlow(false)
        whenever(repository.observeIsFavorite(123)).thenReturn(isFavoriteFlow)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): MovieDetailViewModel =
        MovieDetailViewModel(SavedStateHandle(mapOf("movieId" to 123)), repository)

    @Test
    fun init_loadsMovieDetail_uiStateBecomesSuccess() = runTest(testDispatcher) {
        whenever(repository.getMovieDetail(123)).thenReturn(Resource.Success(movie))

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(Resource.Success::class.java)
        assertThat((state as Resource.Success).data).isEqualTo(movie)
    }

    @Test
    fun init_uiStateStartsAsLoading() = runTest(testDispatcher) {
        whenever(repository.getMovieDetail(123)).thenReturn(Resource.Success(movie))

        val viewModel = createViewModel()

        assertThat(viewModel.uiState.value).isInstanceOf(Resource.Loading::class.java)
    }

    @Test
    fun init_repositoryError_uiStateBecomesError() = runTest(testDispatcher) {
        whenever(repository.getMovieDetail(123)).thenReturn(Resource.Error("Network error"))

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(Resource.Error::class.java)
        assertThat((state as Resource.Error).message).isEqualTo("Network error")
    }

    @Test
    fun isFavorite_defaultsToFalse_thenReflectsRepositoryEmissions() = runTest(testDispatcher) {
        whenever(repository.getMovieDetail(123)).thenReturn(Resource.Success(movie))
        val viewModel = createViewModel()

        turbineScope {
            val turbine = viewModel.isFavorite.testIn(this)
            assertThat(turbine.awaitItem()).isFalse()

            isFavoriteFlow.value = true
            assertThat(turbine.awaitItem()).isTrue()

            turbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun toggleFavorite_notCurrentlyFavorite_addsFavorite() = runTest(testDispatcher) {
        whenever(repository.getMovieDetail(123)).thenReturn(Resource.Success(movie))
        val viewModel = createViewModel()
        advanceUntilIdle()

        turbineScope {
            val turbine = viewModel.isFavorite.testIn(this)
            advanceUntilIdle() // let WhileSubscribed's upstream collection start
            assertThat(viewModel.isFavorite.value).isFalse()

            viewModel.toggleFavorite()
            advanceUntilIdle()

            verify(repository, times(1)).addFavorite(movie)
            verify(repository, times(0)).removeFavorite(123)

            turbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun toggleFavorite_currentlyFavorite_removesFavorite() = runTest(testDispatcher) {
        whenever(repository.getMovieDetail(123)).thenReturn(Resource.Success(movie))
        isFavoriteFlow.value = true
        val viewModel = createViewModel()
        advanceUntilIdle()

        turbineScope {
            val turbine = viewModel.isFavorite.testIn(this)
            advanceUntilIdle() // let WhileSubscribed's upstream collection catch up to the current value
            assertThat(viewModel.isFavorite.value).isTrue()

            viewModel.toggleFavorite()
            advanceUntilIdle()

            verify(repository, times(1)).removeFavorite(123)
            verify(repository, times(0)).addFavorite(movie)

            turbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun toggleFavorite_uiStateNotSuccess_doesNothing() = runTest(testDispatcher) {
        whenever(repository.getMovieDetail(123)).thenReturn(Resource.Error("Network error"))
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.toggleFavorite()
        advanceUntilIdle()

        verify(repository, times(0)).addFavorite(movie)
        verify(repository, times(0)).removeFavorite(123)
    }

    @Test
    fun retry_reloadsMovieDetail() = runTest(testDispatcher) {
        whenever(repository.getMovieDetail(123))
            .thenReturn(Resource.Error("Network error"))
            .thenReturn(Resource.Success(movie))

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.retry()
        advanceUntilIdle()

        verify(repository, times(2)).getMovieDetail(123)
        assertThat(viewModel.uiState.value).isInstanceOf(Resource.Success::class.java)
    }
}

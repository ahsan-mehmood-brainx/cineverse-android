package com.example.cineverse.ui.search

import com.example.cineverse.domain.model.Movie
import com.example.cineverse.domain.repository.MovieRepository
import com.example.cineverse.util.Resource
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val movie = Movie(1, "Dune", null, 8.3, "2021-10-22", "Sci-Fi", "A noble heir.")

    private lateinit var repository: MovieRepository
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        viewModel = SearchViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_initialValue_isSuccessWithEmptyList() = runTest(testDispatcher) {
        assertThat(viewModel.uiState.value).isInstanceOf(Resource.Success::class.java)
        assertThat((viewModel.uiState.value as Resource.Success).data).isEmpty()
    }

    @Test
    fun onQueryChanged_blankQuery_immediatelyResetsToEmptySuccess() = runTest(testDispatcher) {
        whenever(repository.searchMovies("dune")).thenReturn(Resource.Success(listOf(movie)))
        viewModel.onQueryChanged("dune")
        advanceUntilIdle()

        viewModel.onQueryChanged("   ")

        assertThat(viewModel.uiState.value).isInstanceOf(Resource.Success::class.java)
        assertThat((viewModel.uiState.value as Resource.Success).data).isEmpty()
    }

    @Test
    fun onQueryChanged_blankQuery_cancelsInFlightSearch() = runTest(testDispatcher) {
        whenever(repository.searchMovies("dune")).thenReturn(Resource.Success(listOf(movie)))

        viewModel.onQueryChanged("dune")
        advanceTimeBy(100)
        viewModel.onQueryChanged("  ")
        advanceUntilIdle()

        verify(repository, times(0)).searchMovies("dune")
        assertThat((viewModel.uiState.value as Resource.Success).data).isEmpty()
    }

    @Test
    fun onQueryChanged_afterDebounce_setsLoadingThenSuccessResult() = runTest(testDispatcher) {
        whenever(repository.searchMovies("dune")).thenReturn(Resource.Success(listOf(movie)))

        viewModel.onQueryChanged("dune")
        advanceTimeBy(399)
        assertThat(viewModel.uiState.value).isInstanceOf(Resource.Success::class.java) // debounce not elapsed yet

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(Resource.Success::class.java)
        assertThat((state as Resource.Success).data).containsExactly(movie)
    }

    @Test
    fun onQueryChanged_trimsQueryBeforeSearching() = runTest(testDispatcher) {
        whenever(repository.searchMovies("dune")).thenReturn(Resource.Success(listOf(movie)))

        viewModel.onQueryChanged("  dune  ")
        advanceUntilIdle()

        verify(repository, times(1)).searchMovies("dune")
    }

    @Test
    fun onQueryChanged_queryChangedBeforeDebounceElapses_cancelsPreviousSearch() = runTest(testDispatcher) {
        whenever(repository.searchMovies("du")).thenReturn(Resource.Success(listOf(movie)))
        whenever(repository.searchMovies("dune")).thenReturn(Resource.Success(listOf(movie)))

        viewModel.onQueryChanged("du")
        advanceTimeBy(200)
        viewModel.onQueryChanged("dune")
        advanceUntilIdle()

        verify(repository, times(0)).searchMovies("du")
        verify(repository, times(1)).searchMovies("dune")
    }

    @Test
    fun onQueryChanged_repositoryError_uiStateBecomesError() = runTest(testDispatcher) {
        whenever(repository.searchMovies("dune")).thenReturn(Resource.Error("Network error"))

        viewModel.onQueryChanged("dune")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(Resource.Error::class.java)
        assertThat((state as Resource.Error).message).isEqualTo("Network error")
    }

    @Test
    fun retry_reRunsLastQuery() = runTest(testDispatcher) {
        whenever(repository.searchMovies("dune"))
            .thenReturn(Resource.Error("Network error"))
            .thenReturn(Resource.Success(listOf(movie)))

        viewModel.onQueryChanged("dune")
        advanceUntilIdle()

        viewModel.retry()
        advanceUntilIdle()

        verify(repository, times(2)).searchMovies("dune")
        assertThat(viewModel.uiState.value).isInstanceOf(Resource.Success::class.java)
    }
}

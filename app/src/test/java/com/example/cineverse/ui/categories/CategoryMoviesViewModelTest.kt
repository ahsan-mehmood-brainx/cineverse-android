package com.example.cineverse.ui.categories

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
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class CategoryMoviesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val movie = Movie(1, "Dune", null, 8.3, "2021-10-22", "Sci-Fi", "A noble heir.")

    private lateinit var repository: MovieRepository
    private lateinit var viewModel: CategoryMoviesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        viewModel = CategoryMoviesViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun load_popular_fetchesFromRepositoryAndUpdatesUiState() = runTest(testDispatcher) {
        whenever(repository.getPopularMovies()).thenReturn(Resource.Success(listOf(movie)))

        viewModel.load(MovieCategory.POPULAR)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(Resource.Success::class.java)
        assertThat((state as Resource.Success).data).containsExactly(movie)
        verify(repository, times(1)).getPopularMovies()
    }

    @Test
    fun load_topRated_callsGetTopRatedMovies() = runTest(testDispatcher) {
        whenever(repository.getTopRatedMovies()).thenReturn(Resource.Success(listOf(movie)))

        viewModel.load(MovieCategory.TOP_RATED)
        advanceUntilIdle()

        verify(repository, times(1)).getTopRatedMovies()
    }

    @Test
    fun load_upcoming_callsGetUpcomingMovies() = runTest(testDispatcher) {
        whenever(repository.getUpcomingMovies()).thenReturn(Resource.Success(listOf(movie)))

        viewModel.load(MovieCategory.UPCOMING)
        advanceUntilIdle()

        verify(repository, times(1)).getUpcomingMovies()
    }

    @Test
    fun load_nowPlaying_callsGetNowPlayingMovies() = runTest(testDispatcher) {
        whenever(repository.getNowPlayingMovies()).thenReturn(Resource.Success(listOf(movie)))

        viewModel.load(MovieCategory.NOW_PLAYING)
        advanceUntilIdle()

        verify(repository, times(1)).getNowPlayingMovies()
    }

    @Test
    fun load_calledTwiceWithSameCategory_fetchesOnlyOnce() = runTest(testDispatcher) {
        whenever(repository.getPopularMovies()).thenReturn(Resource.Success(listOf(movie)))

        viewModel.load(MovieCategory.POPULAR)
        advanceUntilIdle()
        viewModel.load(MovieCategory.POPULAR)
        advanceUntilIdle()

        verify(repository, times(1)).getPopularMovies()
    }

    @Test
    fun uiState_startsAsLoadingBeforeFetchCompletes() = runTest(testDispatcher) {
        whenever(repository.getPopularMovies()).thenReturn(Resource.Success(listOf(movie)))

        viewModel.load(MovieCategory.POPULAR)

        assertThat(viewModel.uiState.value).isInstanceOf(Resource.Loading::class.java)
    }

    @Test
    fun load_repositoryError_uiStateBecomesError() = runTest(testDispatcher) {
        whenever(repository.getPopularMovies()).thenReturn(Resource.Error("Network error"))

        viewModel.load(MovieCategory.POPULAR)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(Resource.Error::class.java)
        assertThat((state as Resource.Error).message).isEqualTo("Network error")
    }

    @Test
    fun retry_reFetchesLastLoadedCategory() = runTest(testDispatcher) {
        whenever(repository.getPopularMovies())
            .thenReturn(Resource.Error("Network error"))
            .thenReturn(Resource.Success(listOf(movie)))

        viewModel.load(MovieCategory.POPULAR)
        advanceUntilIdle()

        viewModel.retry()
        advanceUntilIdle()

        verify(repository, times(2)).getPopularMovies()
        assertThat(viewModel.uiState.value).isInstanceOf(Resource.Success::class.java)
    }

    @Test
    fun retry_withoutPriorLoad_doesNotCallRepository() = runTest(testDispatcher) {
        viewModel.retry()
        advanceUntilIdle()

        verify(repository, times(0)).getPopularMovies()
        verify(repository, times(0)).getTopRatedMovies()
        verify(repository, times(0)).getUpcomingMovies()
        verify(repository, times(0)).getNowPlayingMovies()
    }
}

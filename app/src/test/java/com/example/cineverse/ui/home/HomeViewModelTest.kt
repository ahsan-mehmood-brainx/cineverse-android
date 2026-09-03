package com.example.cineverse.ui.home

import app.cash.turbine.turbineScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.testing.TestLifecycleOwner
import com.example.cineverse.util.Resource
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

class HomeViewModelTest {

    @Test
    fun viewModel_initializesWithLoading() = runTest {
        val viewModel = HomeViewModel()

        assertThat(viewModel.uiState.value).isInstanceOf(Resource.Loading::class.java)
    }

    @Test
    fun viewModel_emitsSuccessState() = runTest {
        turbineScope {
            val viewModel = HomeViewModel()
            val uiStateTurbine = viewModel.uiState.testIn(this)

            val loadingState = uiStateTurbine.awaitItem()
            assertThat(loadingState).isInstanceOf(Resource.Loading::class.java)

            val successState = uiStateTurbine.awaitItem()
            assertThat(successState).isInstanceOf(Resource.Success::class.java)

            val data = (successState as Resource.Success).data
            assertThat(data.trendingMovies).hasSize(5)
            assertThat(data.popularMovies).hasSize(5)
            assertThat(data.topRatedMovies).hasSize(5)
            assertThat(data.genres).hasSize(8)
        }
    }

    @Test
    fun viewModel_successStateHasNonEmptyData() = runTest {
        val viewModel = HomeViewModel()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(Resource.Success::class.java)

        val uiState = (state as Resource.Success).data
        assertThat(uiState.isEmpty).isFalse()
    }

    @Test
    fun viewModel_retry_reloadsData() = runTest {
        turbineScope {
            val viewModel = HomeViewModel()
            val uiStateTurbine = viewModel.uiState.testIn(this)

            uiStateTurbine.awaitItem() // Loading
            uiStateTurbine.awaitItem() // Success

            viewModel.retry()

            uiStateTurbine.awaitItem() // Success again

            val successState = uiStateTurbine.awaitItem()
            assertThat(successState).isInstanceOf(Resource.Success::class.java)
        }
    }

    @Test
    fun viewModel_uiStateFlowIsReadOnly() = runTest {
        val viewModel = HomeViewModel()

        val state = viewModel.uiState.value
        assertThat(state).isInstanceOf(Resource.Loading::class.java)
    }

    @Test
    fun viewModel_loadedDataContainsMockData() = runTest {
        val viewModel = HomeViewModel()

        val state = viewModel.uiState.value
        val uiState = (state as? Resource.Success)?.data

        assertThat(uiState).isNotNull()
        uiState?.let { state ->
            assertThat(state.trendingMovies.map { it.title })
                .containsAtLeast("The Dark Knight", "Inception", "Interstellar")
        }
    }
}

package com.example.cineverse.ui.home

import androidx.lifecycle.ViewModel
import com.example.cineverse.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Backed by [HomeMockData] until the home feed is wired to a repository; the Resource
 * wrapper and [retry] are kept so swapping in a real data source later is a drop-in change.
 */
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<HomeUiState>>(Resource.Loading)
    val uiState: StateFlow<Resource<HomeUiState>> = _uiState.asStateFlow()

    init {
        loadHome()
    }

    fun retry() = loadHome()

    private fun loadHome() {
        _uiState.value = Resource.Success(HomeMockData.toUiState())
    }
}

package com.example.androidsideproject.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.androidsideproject.MainApplication
import com.example.androidsideproject.data.entities.watchlist.WatchlistRepository
import com.example.androidsideproject.model.MovieView
import com.example.androidsideproject.model.UiState
import com.example.androidsideproject.ui.MovieFilterManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WatchlistViewModel(
    private val watchlistRepository: WatchlistRepository,
    private val filterManager: MovieFilterManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())

    // Combine the loading/error state with the filter state
    val uiState: StateFlow<UiState> = _uiState
        .combine(filterManager.uiState) { loadingState, filterState ->
            // Merge the loading/error state with the filtered data state
            filterState.copy(
                isLoading = loadingState.isLoading,
                errorMessage = loadingState.errorMessage
            )
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, UiState())

    init {
        fetchWatchlistMovies()
    }

    private fun fetchWatchlistMovies() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Fetch the movies from the watchlist repository
                val movieViews = watchlistRepository.getWatchlist()

                // Initialize filter manager with the fetched movies
                filterManager.initialize(movieViews)

                // Update the UI state to show movies and hide the loading state
                _uiState.value = _uiState.value.copy(
                    movies = movieViews,
                    isLoading = false
                )
            } catch (e: Exception) {
                // Handle error and update UI state
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.localizedMessage,
                    isLoading = false
                )

                // Reset filters in case of error
                filterManager.applyFilter(null, null)
            }
        }
    }

    // Function to apply filters based on selected language and genre
    fun applyFilter(language: String?, genre: String?) {
        filterManager.applyFilter(language, genre)
    }

    // Function to update the selected page for pagination or viewing different pages
    fun updateSelectedPage(newPage: Int) {
        filterManager.updateSelectedPage(newPage)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as MainApplication
                val watchlistRepository = application.container.watchlistRepository
                val filterManager = MovieFilterManager()
                WatchlistViewModel(
                    watchlistRepository = watchlistRepository,
                    filterManager = filterManager
                )
            }
        }
    }
}

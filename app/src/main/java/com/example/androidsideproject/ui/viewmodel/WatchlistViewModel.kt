package com.example.androidsideproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.androidsideproject.MainApplication
import com.example.androidsideproject.data.entities.watchlist.WatchlistRepository
import com.example.androidsideproject.model.UiState
import com.example.androidsideproject.ui.MovieFilterManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WatchlistViewModel(
    private val watchlistRepository: WatchlistRepository,
    private val filterManager: MovieFilterManager
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _uiState = MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> = _uiState
        .combine(filterManager.uiState) { loadingState, filterState ->
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
                watchlistRepository.getWatchlist().collect { movieViews ->
                    filterManager.initialize(movieViews)

                    _uiState.value = _uiState.value.copy(
                        movies = movieViews,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.localizedMessage,
                    isLoading = false
                )

                filterManager.applyFilter(null, null)
            }
        }
    }

    fun applyFilter(language: String?, genre: String?) {
        filterManager.applyFilter(language, genre)
    }

    fun updateSelectedPage(newPage: Int) {
        filterManager.updateSelectedPage(newPage)
    }

    fun updateRating(movieId: Long, rating: Int) {
        viewModelScope.launch {
            try {
                watchlistRepository.updateRating(movieId, rating)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update rating: ${e.localizedMessage}"
                )
            }
        }
    }

    fun deleteMovieFromWatchlist(movieId: Long) {
        viewModelScope.launch {
            try {
                watchlistRepository.deleteMovie(movieId)
                _navigationEvent.emit("watchlist")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to remove movie from watchlist: ${e.localizedMessage}"
                )
            }
        }
    }

    fun resetNavigationEvent() {
        viewModelScope.launch {
            _navigationEvent.emit("")
        }
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

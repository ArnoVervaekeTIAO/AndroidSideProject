package com.example.androidsideproject.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.androidsideproject.MainApplication
import com.example.androidsideproject.data.entities.genre.GenreRepository
import com.example.androidsideproject.data.entities.language.LanguageRepository
import com.example.androidsideproject.data.entities.movie.MovieRepository
import com.example.androidsideproject.data.entities.watchlist.WatchlistRepository
import com.example.androidsideproject.model.MovieView
import com.example.androidsideproject.model.UiState
import com.example.androidsideproject.ui.MovieFilterManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BrowseViewModel(
    private val movieRepository: MovieRepository,
    private val genreRepository: GenreRepository,
    private val languageRepository: LanguageRepository,
    private val filterManager: MovieFilterManager,
    private val watchlistRepository: WatchlistRepository,
    private val applicationContext: Context
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    val _uiState = MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> = filterManager.uiState
        .combine(_uiState) { filterState, loadingState ->
            filterState.copy(
                isLoading = loadingState.isLoading,
                errorMessage = loadingState.errorMessage
            )
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, UiState())

    init {
        observeMoviesAndWatchlist()
    }

    fun observeMoviesAndWatchlist() {
        viewModelScope.launch {
            try {

                if (!isNetworkAvailable(applicationContext)) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "No internet connection"
                    )
                    return@launch
                }

                val genresFlow = genreRepository.getGenres()
                val languageFlow = languageRepository.getLanguages()
                val moviesFlow = movieRepository.getMovies()
                val watchlistFlow = watchlistRepository.getWatchlist()

                combine(moviesFlow, watchlistFlow) { movies, watchlistMovies ->
                    val genresList = genresFlow.first()
                    val languageList = languageFlow.first()

                    val mappedMovies = movies.map { movie ->
                        MovieView(
                            id = movie.id,
                            title = movie.title,
                            overview = movie.overview,
                            language = languageList.find { it.id == movie.originalLanguage }?.name ?: "Unknown",
                            genreNames = movie.genreIds.map { genreId ->
                                genresList.find { it.id == genreId }?.name ?: "Unknown"
                            }
                        )
                    }

                    val filteredMovies = mappedMovies.filter { movieView ->
                        watchlistMovies.none { it.id == movieView.id }
                    }

                    filterManager.initialize(filteredMovies)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        movies = filteredMovies
                    )
                }.collect { }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage
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

    fun addMovieToWatchlist(movieView: MovieView) {
        viewModelScope.launch {
            try {
                watchlistRepository.addToWatchlist(movieView)
                _navigationEvent.emit("watchlist")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to add to watchlist: ${e.localizedMessage}"
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
                val movieRepository = application.container.movieRepository
                val genreRepository = application.container.genreRepository
                val languageRepository = application.container.languageRepository
                val watchlistRepository = application.container.watchlistRepository
                val filterManager = MovieFilterManager()
                val applicationContext = application.applicationContext
                BrowseViewModel(
                    movieRepository = movieRepository,
                    genreRepository = genreRepository,
                    languageRepository = languageRepository,
                    watchlistRepository = watchlistRepository,
                    filterManager = filterManager,
                    applicationContext = applicationContext
                )
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork?.let { network ->
            connectivityManager.getNetworkCapabilities(network)
        }

        return networkCapabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
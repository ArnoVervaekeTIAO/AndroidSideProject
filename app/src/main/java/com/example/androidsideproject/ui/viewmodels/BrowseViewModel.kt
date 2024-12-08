package com.example.androidsideproject.ui.viewmodels

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
import com.example.androidsideproject.model.MovieView
import com.example.androidsideproject.model.UiState
import com.example.androidsideproject.ui.MovieFilterManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BrowseViewModel(
    private val movieRepository: MovieRepository,
    private val genreRepository: GenreRepository,
    private val languageRepository: LanguageRepository,
    private val filterManager: MovieFilterManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> = filterManager.uiState
        .combine(_uiState) { filterState, loadingState ->
            filterState.copy(
                isLoading = loadingState.isLoading,
                errorMessage = loadingState.errorMessage
            )
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, UiState())

    init {
        fetchMovies()
    }

    private fun fetchMovies() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val genresFlow = genreRepository.getGenres()
                val languageFlow = languageRepository.getLanguages()
                val movieViews = movieRepository.getMovies().map { movieList ->
                    val genresList = genresFlow.first()
                    val languageList = languageFlow.first()
                    movieList.map { movie ->
                        MovieView(
                            id = movie.id,
                            title = movie.title,
                            overview = movie.overview,
                            language = languageList.find { it.id == movie.originalLanguage }?.name
                                ?: "Unknown",
                            genreNames = movie.genreIds.map { genreId ->
                                genresList.find { it.id == genreId }?.name ?: "Unknown"
                            }
                        )
                    }
                }.first()

                filterManager.initialize(movieViews)

                _uiState.value = _uiState.value.copy(isLoading = false)
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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as MainApplication
                val movieRepository = application.container.movieRepository
                val genreRepository = application.container.genreRepository
                val languageRepository = application.container.languageRepository
                val filterManager = MovieFilterManager()
                BrowseViewModel(
                    movieRepository = movieRepository,
                    genreRepository = genreRepository,
                    languageRepository = languageRepository,
                    filterManager = filterManager
                )
            }
        }
    }
}


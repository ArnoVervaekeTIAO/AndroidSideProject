package com.example.androidsideproject.ui.movie

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MovieViewModel(
    private val movieRepository: MovieRepository,
    private val genreRepository: GenreRepository,
    private val languageRepository: LanguageRepository
) : ViewModel() {
    private val _movies = MutableStateFlow<List<MovieView>>(emptyList())
    val movies: StateFlow<List<MovieView>> get() = _movies

    init {
        fetchMovies()
    }

    private fun fetchMovies() {
        viewModelScope.launch {
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
                        language = languageList.find { it.id == movie.originalLanguage }?.name ?: "Unknown",
                        genreNames = movie.genreIds.map { genreId ->
                            genresList.find { it.id == genreId }?.name ?: "Unknown"
                        }
                    )
                }
            }.first()

            _movies.value = movieViews
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as MainApplication
                val movieRepository = application.container.movieRepository
                val genreRepository = application.container.genreRepository
                val languageRepository = application.container.languageRepository
                MovieViewModel(
                    movieRepository = movieRepository,
                    genreRepository = genreRepository,
                    languageRepository = languageRepository
                )
            }
        }
    }
}



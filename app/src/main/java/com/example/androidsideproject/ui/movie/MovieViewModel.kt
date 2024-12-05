package com.example.androidsideproject.ui.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.androidsideproject.MainApplication
import com.example.androidsideproject.data.entities.genre.GenreRepository
import com.example.androidsideproject.data.entities.movies.MovieRepository
import com.example.androidsideproject.model.MovieWithGenres
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MovieViewModel(
    private val movieRepository: MovieRepository,
    private val genreRepository: GenreRepository
) : ViewModel() {
    private val _movies = MutableStateFlow<List<MovieWithGenres>>(emptyList())
    val movies: StateFlow<List<MovieWithGenres>> get() = _movies

    init {
        fetchMovies()
    }

    private fun fetchMovies() {
        viewModelScope.launch {
            val genresFlow = genreRepository.getGenres()
            val moviesWithGenres = movieRepository.getMovies().map { movieList ->
                val genresList = genresFlow.first()
                movieList.map { movie ->
                    MovieWithGenres(
                        id = movie.id,
                        title = movie.title,
                        overview = movie.overview,
                        originalLanguage = movie.originalLanguage,
                        genreNames = movie.genreIds.map { genreId ->
                            genresList.find { it.id == genreId }?.name ?: "Unknown"
                        }
                    )
                }
            }.first()

            _movies.value = moviesWithGenres
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as MainApplication
                val movieRepository = application.container.movieRepository
                val genreRepository = application.container.genreRepository
                MovieViewModel(movieRepository = movieRepository, genreRepository = genreRepository)
            }
        }
    }
}



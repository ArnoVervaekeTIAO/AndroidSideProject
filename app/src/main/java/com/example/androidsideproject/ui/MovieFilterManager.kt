package com.example.androidsideproject.ui

import com.example.androidsideproject.model.MovieView
import com.example.androidsideproject.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MovieFilterManager {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun initialize(movies: List<MovieView>) {
        _uiState.value = _uiState.value.copy(
            movies = movies,
            filteredMovies = movies
        )
    }

    fun applyFilter(language: String?, genre: String?) {
        val filtered = when {
            language == null && genre == null -> _uiState.value.movies
            else -> _uiState.value.movies.filter { movie ->
                (language == null || movie.language == language) &&
                        (genre == null || genre in movie.genreNames)
            }
        }

        _uiState.value = _uiState.value.copy(
            selectedLanguage = language,
            selectedGenre = genre,
            filteredMovies = filtered,
            selectedPage = 0
        )
    }

    fun updateSelectedPage(newPage: Int) {
        _uiState.value = _uiState.value.copy(selectedPage = newPage)
    }
}

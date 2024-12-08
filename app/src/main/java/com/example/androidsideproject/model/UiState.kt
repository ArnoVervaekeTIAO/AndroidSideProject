package com.example.androidsideproject.model

data class UiState(
    val isLoading: Boolean = false,
    val movies: List<MovieView> = emptyList(),
    val errorMessage: String? = null,
    val filteredMovies: List<MovieView> = listOf(),
    val selectedPage: Int = 0,
    val selectedLanguage: String? = null,
    val selectedGenre: String? = null
)
package com.example.androidsideproject.model

data class MovieWithGenres(
    val id: Int,
    val title: String,
    val overview: String,
    val originalLanguage: String,
    val genreNames: List<String>
)
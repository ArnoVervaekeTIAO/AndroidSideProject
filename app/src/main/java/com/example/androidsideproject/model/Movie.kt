package com.example.androidsideproject.model

data class Movie(
    val id: Int,
    val title: String,
    val genreIds: List<Int>,
    val overview: String,
    val originalLanguage: String
)
package com.example.androidsideproject.model

data class MovieView(
    val id: Int,
    val title: String,
    val overview: String,
    val language: String,
    val genreNames: List<String>
)
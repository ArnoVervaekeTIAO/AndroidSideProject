package com.example.androidsideproject.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class MovieView(
    val id: Int,
    val title: String,
    val overview: String,
    val language: String,
    val genreNames: List<String>,
    val rating: Int? = null
)
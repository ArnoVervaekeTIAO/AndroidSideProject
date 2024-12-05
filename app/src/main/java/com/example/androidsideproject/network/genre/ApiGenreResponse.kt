package com.example.androidsideproject.network.genre

import kotlinx.serialization.Serializable

@Serializable
data class ApiGenreResponse(
    val genres: List<ApiGenre>
)
package com.example.androidsideproject.network.movie

import kotlinx.serialization.Serializable

@Serializable
data class ApiMovieResponse(
    val page: Int,
    val results: List<ApiMovie>,
    val total_pages: Int,
    val total_results: Int
)
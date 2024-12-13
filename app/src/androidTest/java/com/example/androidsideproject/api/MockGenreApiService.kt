package com.example.androidsideproject.api

import com.example.androidsideproject.model.Genre
import com.example.androidsideproject.network.genre.ApiGenreResponse
import com.example.androidsideproject.network.genre.ApiGenre
import com.example.androidsideproject.network.genre.GenreApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockGenreApiService : GenreApiService {
    override suspend fun getGenres(apiKey: String): ApiGenreResponse {
        return ApiGenreResponse(
            genres = listOf(
                ApiGenre(id = 1, name = "Action"),
                ApiGenre(id = 2, name = "Drama")
            )
        )
    }
}

fun ApiGenreResponse.asDomainObjects(): List<Genre> {
    return genres.map {
        Genre(id = it.id, name = it.name)
    }
}

fun GenreApiService.getGenresAsFlow(apiKey: String): Flow<List<Genre>> =
    flow {
        val response = getGenres(apiKey)
        emit(response.asDomainObjects())
    }


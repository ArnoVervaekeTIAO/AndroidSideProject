package com.example.androidsideproject.network.genre

import com.example.androidsideproject.model.Genre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.GET
import retrofit2.http.Query

interface GenreApiService {
    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") apiKey: String
    ): ApiGenreResponse
}

fun GenreApiService.getGenresAsFlow(apiKey: String): Flow<List<Genre>> =
    flow {
        val response = getGenres(apiKey)
        emit(response.genres.asDomainObjects())
    }
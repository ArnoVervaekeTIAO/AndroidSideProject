package com.example.androidsideproject.network.movie

import com.example.androidsideproject.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): ApiMovieResponse
}

fun MovieApiService.getPopularMoviesAsFlow(apiKey: String, page: Int = 1): Flow<List<Movie>> =
    flow {
        val response = getPopularMovies(apiKey, page)
        emit(response.results.asDomainObjects())
    }
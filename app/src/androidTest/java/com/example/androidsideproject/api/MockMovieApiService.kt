package com.example.androidsideproject.api

import com.example.androidsideproject.network.movie.ApiMovie
import com.example.androidsideproject.network.movie.ApiMovieResponse
import com.example.androidsideproject.network.movie.MovieApiService
import com.example.androidsideproject.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockMovieApiService : MovieApiService {
    override suspend fun getPopularMovies(apiKey: String, page: Int): ApiMovieResponse {
        return ApiMovieResponse(
            page = page,
            results = listOf(
                ApiMovie(
                    id = 1,
                    title = "The Dark Knight",
                    overview = "Overview1",
                    genreIds = listOf(280, 800, 180),
                    originalLanguage = "en"
                ),
                ApiMovie(
                    id = 2,
                    title = "Inception",
                    overview = "Overview2",
                    genreIds = listOf(280, 120, 180),
                    originalLanguage = "en"
                )
            ),
            total_pages = 1,
            total_results = 2
        )
    }
}

fun ApiMovieResponse.asDomainObjects(): List<Movie> {
    return results.map {
        Movie(
            id = it.id,
            title = it.title,
            overview = it.overview,
            genreIds = it.genreIds,
            originalLanguage = it.originalLanguage
        )
    }
}

fun MovieApiService.getPopularMoviesAsFlow(apiKey: String, page: Int = 1): Flow<List<Movie>> =
    flow {
        val response = getPopularMovies(apiKey, page)
        emit(response.asDomainObjects())
    }

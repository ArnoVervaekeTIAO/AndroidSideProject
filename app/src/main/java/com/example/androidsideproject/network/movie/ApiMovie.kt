package com.example.androidsideproject.network.movie

import com.example.androidsideproject.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiMovie(
    val id: Int,
    val title: String,
    val overview: String,
    @SerialName("genre_ids") val genreIds: List<Int>,
    @SerialName("original_language") val originalLanguage: String
)

fun List<ApiMovie>.asDomainObjects(): List<Movie> = map {
    Movie(
        id = it.id,
        title = it.title,
        overview = it.overview,
        genreIds = it.genreIds,
        originalLanguage = it.originalLanguage
    )
}

fun ApiMovie.asDomainObject(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    genreIds = genreIds,
    originalLanguage = originalLanguage
)

fun Flow<List<ApiMovie>>.asDomainObjects(): Flow<List<Movie>> {
    return this.map {
        it.asDomainObjects()
    }
}

fun Flow<ApiMovie>.asDomainObject(): Flow<Movie> {
    return this.map {
        it.asDomainObject()
    }
}
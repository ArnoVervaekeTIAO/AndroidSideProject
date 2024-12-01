package com.example.androidsideproject.network.movie

import com.example.androidsideproject.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

@Serializable
data class ApiMovie(
    val id: Int,
    val title: String,
)

fun List<ApiMovie>.asDomainObjects(): List<Movie> = map {
    Movie(
        id = it.id,
        title = it.title,
    )
}

fun ApiMovie.asDomainObject(): Movie = Movie(
    id = id,
    title = title,
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
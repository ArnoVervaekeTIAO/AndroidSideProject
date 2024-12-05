package com.example.androidsideproject.network.genre

import com.example.androidsideproject.model.Genre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

@Serializable
data class ApiGenre(
    val id: Int,
    val name: String,
)

fun List<ApiGenre>.asDomainObjects(): List<Genre> = map {
    Genre(
        id = it.id,
        name = it.name,
    )
}

fun ApiGenre.asDomainObject(): Genre = Genre(
    id = id,
    name = name,
)

fun Flow<List<ApiGenre>>.asDomainObjects(): Flow<List<Genre>> {
    return this.map {
        it.asDomainObjects()
    }
}

fun Flow<ApiGenre>.asDomainObject(): Flow<Genre> {
    return this.map {
        it.asDomainObject()
    }
}


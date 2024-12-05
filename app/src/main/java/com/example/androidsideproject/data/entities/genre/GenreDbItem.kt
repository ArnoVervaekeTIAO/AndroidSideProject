package com.example.androidsideproject.data.entities.genre

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidsideproject.model.Genre


@Entity(tableName = "genre")
data class GenreDbItem(
    @PrimaryKey
    var id: Int,
    var name: String
)

fun Genre.getAsGenreDbItem(): GenreDbItem = GenreDbItem(
    id = id,
    name = name,
)


fun GenreDbItem.asDomainGenre(): Genre = Genre(
    id = id,
    name = name,
)

fun List<GenreDbItem>.asDomainGenres(): List<Genre> {
    return map { it.asDomainGenre() }
}
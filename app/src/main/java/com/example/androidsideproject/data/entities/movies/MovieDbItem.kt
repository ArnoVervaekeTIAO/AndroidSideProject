package com.example.androidsideproject.data.entities.movies

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidsideproject.model.Movie

@Entity(tableName = "movie")
data class MovieDbItem(
    @PrimaryKey
    var id: Int,
    var title: String,
)

fun Movie.getAsMovieDbItem(): MovieDbItem = MovieDbItem(
    id = id,
    title = title,
)


fun MovieDbItem.asDomainMovie(): Movie = Movie(
    id = id,
    title = title,
)

fun List<MovieDbItem>.asDomainMovies() = map { it.asDomainMovie() }
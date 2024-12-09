package com.example.androidsideproject.data.entities.watchlist

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidsideproject.model.MovieView

@Entity(tableName = "watchlist")
data class WatchlistDbItem(
    @PrimaryKey
    var id: Int,
    var title: String,
    var overview: String,
    var genreNames: String,
    var language: String,
    var rating: Int?
)

fun MovieView.toWatchlistDbItem(): WatchlistDbItem = WatchlistDbItem(
    id = id,
    title = title,
    overview = overview,
    genreNames = genreNames.joinToString(", "),
    language = language,
    rating = rating
)

fun WatchlistDbItem.toMovieView(): MovieView = MovieView(
    id = id,
    title = title,
    overview = overview,
    language = language,
    genreNames = genreNames.split(", ").filter { it.isNotEmpty() },
    rating = rating
)
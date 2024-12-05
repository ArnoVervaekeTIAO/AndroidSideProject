package com.example.androidsideproject.data.entities.movies

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidsideproject.model.Movie

@Entity(tableName = "movie")
data class MovieDbItem(
    @PrimaryKey
    var id: Int,
    var title: String,
    var overview: String,
    var originalLanguage: String
)

fun Movie.getAsMovieDbItem(): MovieDbItem = MovieDbItem(
    id = id,
    title = title,
    overview = overview,
    originalLanguage = originalLanguage
)


fun MovieDbItem.asDomainMovie(genreIds: List<Int>): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    originalLanguage = originalLanguage,
    genreIds = genreIds
)

fun List<MovieDbItem>.asDomainMovies(crossRefs: List<MovieGenreCrossRef>): List<Movie> {
    val genreMap = crossRefs.groupBy({ it.movieId }, { it.genreId })

    return map { movieDbItem ->
        val genreIds = genreMap[movieDbItem.id] ?: emptyList()
        movieDbItem.asDomainMovie(genreIds)
    }
}
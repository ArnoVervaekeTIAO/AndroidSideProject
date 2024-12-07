package com.example.androidsideproject.data.entities.movie

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidsideproject.model.Movie

@Entity(tableName = "movie")
data class MovieDbItem(
    @PrimaryKey
    var id: Int,
    var title: String,
    var overview: String,
)

fun Movie.getAsMovieDbItem(): MovieDbItem = MovieDbItem(
    id = id,
    title = title,
    overview = overview,
)


fun MovieDbItem.asDomainMovie(genreIds: List<Int>, language: String): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    originalLanguage = language,
    genreIds = genreIds
)

fun List<MovieDbItem>.asDomainMovies(genreCrossRefs: List<MovieGenreCrossRef>, languageCrossRefs: List<MovieLanguageCrossRef>): List<Movie> {
    val genreMap = genreCrossRefs.groupBy({ it.movieId }, { it.genreId })
    val languageMap = languageCrossRefs.groupBy({  it.movieId }, { it.languageId })

    return map { movieDbItem ->
        val genreIds = genreMap[movieDbItem.id] ?: emptyList()
        val language = languageMap[movieDbItem.id]!!.first()
        movieDbItem.asDomainMovie(genreIds, language)
    }
}
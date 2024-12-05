package com.example.androidsideproject.data.entities.movies

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.androidsideproject.data.entities.genre.GenreDbItem

@Entity(
    tableName = "movie_genre_cross_ref",
    primaryKeys = ["movieId", "genreId"],
    foreignKeys = [
        ForeignKey(
            entity = MovieDbItem::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GenreDbItem::class,
            parentColumns = ["id"],
            childColumns = ["genreId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MovieGenreCrossRef(
    val movieId: Int,
    val genreId: Int
)
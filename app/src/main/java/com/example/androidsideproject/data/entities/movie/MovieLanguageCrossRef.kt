package com.example.androidsideproject.data.entities.movie

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.androidsideproject.data.entities.language.LanguageDbItem

@Entity(
    tableName = "movie_language_cross_ref",
    primaryKeys = ["movieId", "languageId"],
    foreignKeys = [
        ForeignKey(
            entity = MovieDbItem::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LanguageDbItem::class,
            parentColumns = ["id"],
            childColumns = ["languageId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MovieLanguageCrossRef(
    val movieId: Int,
    val languageId: String
)
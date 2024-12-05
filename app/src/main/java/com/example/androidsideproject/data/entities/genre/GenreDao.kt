package com.example.androidsideproject.data.entities.genre

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<GenreDbItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenre(genre: GenreDbItem)

    @Query("SELECT * FROM genre")
    fun getAllGenres(): Flow<List<GenreDbItem>>

    @Query("SELECT * FROM genre WHERE id = :genreId LIMIT 1")
    suspend fun getGenreById(genreId: Int): GenreDbItem?
}
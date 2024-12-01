package com.example.androidsideproject.data.entities.movies

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MovieDbItem)

    @Query("SELECT * FROM movie ORDER BY id ASC")
    fun getMovies(): Flow<List<MovieDbItem>>
}
package com.example.androidsideproject.data.entities.watchlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlist(movie: WatchlistDbItem)

    @Query("SELECT * FROM watchlist")
    fun getAllWatchlists(): Flow<List<WatchlistDbItem>>

    @Query("UPDATE watchlist SET rating = :rating WHERE id = :movieId")
    suspend fun updateRating(movieId: Long, rating: Int)

    @Query("DELETE FROM watchlist WHERE id = :movieId")
    suspend fun deleteMovie(movieId: Long)
}
package com.example.androidsideproject.data.entities.watchlist

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WatchlistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlist(movie: WatchlistDbItem)

    @Query("SELECT * FROM watchlist")
    suspend fun getAllWatchlists(): List<WatchlistDbItem>
}
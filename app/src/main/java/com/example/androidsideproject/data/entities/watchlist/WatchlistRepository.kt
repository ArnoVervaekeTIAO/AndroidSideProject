package com.example.androidsideproject.data.entities.watchlist

import com.example.androidsideproject.model.MovieView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WatchlistRepository(private val watchlistDao: WatchlistDao) {

    suspend fun addToWatchlist(movieView: MovieView) {
        val watchlistItem = movieView.toWatchlistDbItem()
        watchlistDao.insertWatchlist(watchlistItem)
    }

    fun getWatchlist(): Flow<List<MovieView>> {
        return watchlistDao.getAllWatchlists().map { list ->
            list.map { it.toMovieView() }
        }
    }

    suspend fun updateRating(movieId: Long, rating: Int) {
        watchlistDao.updateRating(movieId, rating)
    }

    suspend fun deleteMovie(movieId: Long) {
        watchlistDao.deleteMovie(movieId)
    }
}

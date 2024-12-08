package com.example.androidsideproject.data.entities.watchlist

import com.example.androidsideproject.model.MovieView

class WatchlistRepository(private val watchlistDao: WatchlistDao) {

    suspend fun addToWatchlist(movieView: MovieView) {
        val watchlistItem = movieView.toWatchlistDbItem()
        watchlistDao.insertWatchlist(watchlistItem)
    }

    suspend fun getWatchlist(): List<MovieView> {
        val watchlistDbItems = watchlistDao.getAllWatchlists()
        return watchlistDbItems.map { it.toMovieView() }
    }
}

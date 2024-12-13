package com.example.androidsideproject.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.androidsideproject.data.database.MainDatabase
import com.example.androidsideproject.data.entities.watchlist.WatchlistDbItem
import com.example.androidsideproject.data.entities.watchlist.WatchlistRepository
import com.example.androidsideproject.data.entities.watchlist.WatchlistDao
import com.example.androidsideproject.model.MovieView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WatchlistRepositoryTest {

    private lateinit var database: MainDatabase
    private lateinit var watchlistDao: WatchlistDao
    private lateinit var repository: WatchlistRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MainDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        watchlistDao = database.watchlistDao()

        repository = WatchlistRepository(watchlistDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addToWatchlistShouldInsertMovie() = runBlocking {
        // Given
        val movieView = MovieView(
            id = 1,
            title = "Inception",
            overview = "A dream within a dream",
            genreNames = listOf("Sci-Fi", "Thriller"),
            language = "en",
            rating = 5
        )

        // When
        repository.addToWatchlist(movieView)

        // Then
        val watchlistMovies = watchlistDao.getAllWatchlists().first()
        assertEquals(1, watchlistMovies.size)
        assertEquals(movieView.id, watchlistMovies[0].id)
        assertEquals(movieView.title, watchlistMovies[0].title)
        assertEquals(movieView.rating, watchlistMovies[0].rating)
    }

    @Test
    fun getWatchlistShouldRetrieveMovies() = runBlocking {
        // Given
        val movie1 = MovieView(id = 1, title = "Inception", overview = "A dream within a dream", genreNames = listOf("Sci-Fi", "Thriller"), language = "en", rating = 5)
        val movie2 = MovieView(id = 2, title = "The Dark Knight", overview = "A hero's journey", genreNames = listOf("Action", "Crime"), language = "en", rating = 4)

        repository.addToWatchlist(movie1)
        repository.addToWatchlist(movie2)

        // When
        val watchlistMovies = repository.getWatchlist().first()

        // Then
        assertEquals(2, watchlistMovies.size)
        assertEquals("Inception", watchlistMovies[0].title)
        assertEquals("The Dark Knight", watchlistMovies[1].title)
    }

    @Test
    fun updateRatingShouldUpdateMovieRating() = runBlocking {
        // Given
        val movieView = MovieView(id = 1, title = "Inception", overview = "A dream within a dream", genreNames = listOf("Sci-Fi", "Thriller"), language = "en", rating = 5)
        repository.addToWatchlist(movieView)

        // When
        repository.updateRating(1, 8)

        // Then
        val updatedMovie = watchlistDao.getAllWatchlists().first().first { it.id == 1 }
        assertEquals(8, updatedMovie.rating)
    }

    @Test
    fun deleteMovieShouldRemoveMovieFromWatchlist() = runBlocking {
        // Given
        val movieView = MovieView(id = 1, title = "Inception", overview = "A dream within a dream", genreNames = listOf("Sci-Fi", "Thriller"), language = "en", rating = 5)
        repository.addToWatchlist(movieView)

        // When
        repository.deleteMovie(1)

        // Then
        val watchlistMovies = watchlistDao.getAllWatchlists().first()
        assertTrue(watchlistMovies.isEmpty())
    }
}

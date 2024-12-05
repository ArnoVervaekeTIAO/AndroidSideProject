package com.example.androidsideproject.data.entities.movies

import android.util.Log
import com.example.androidsideproject.BuildConfig
import com.example.androidsideproject.data.entities.genre.GenreDao
import com.example.androidsideproject.data.entities.genre.getAsGenreDbItem
import com.example.androidsideproject.model.Movie
import com.example.androidsideproject.network.genre.GenreApiService
import com.example.androidsideproject.network.genre.getGenresAsFlow
import com.example.androidsideproject.network.movie.MovieApiService
import com.example.androidsideproject.network.movie.getPopularMoviesAsFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface MovieRepository {
    fun getMovies(): Flow<List<Movie>>
    suspend fun insert(item: Movie)
    suspend fun refresh()
}

class CachingMovieRepository(
    private val movieDao: MovieDao,
    private val genreDao: GenreDao,
    private val movieApiService: MovieApiService,
    private val genreApiService: GenreApiService
) : MovieRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.IO) // Create a CoroutineScope for background tasks

    init {
        // Start fetching data in the background
        coroutineScope.launch {
            refresh() // Fetch and insert genres and movies asynchronously
        }
    }

    override fun getMovies(): Flow<List<Movie>> {
        return movieDao.getMovies().map { movieDbItems ->
            val crossRefs = movieDao.getAllMovieGenreCrossRefs()
            movieDbItems.asDomainMovies(crossRefs)
        }.onEach {
            if (it.isEmpty()) {
                refresh()
            }
        }
    }

    override suspend fun insert(item: Movie) {
        movieDao.insertMovie(item.getAsMovieDbItem())
        val crossRefs = item.genreIds.map { genreId ->
            MovieGenreCrossRef(movieId = item.id, genreId = genreId)
        }
        movieDao.insertMovieGenreCrossRefs(crossRefs)
    }

    override suspend fun refresh() {
        try {
            genreApiService.getGenresAsFlow(BuildConfig.API_KEY).collect { genres ->
                for (genre in genres)
                {
                    val existingGenre = genreDao.getGenreById(genre.id)
                    if (existingGenre == null) {
                        genreDao.insertGenre(genre.getAsGenreDbItem())
                    }
                }
            }

            movieApiService.getPopularMoviesAsFlow(BuildConfig.API_KEY).collect { movies ->
                for (movie in movies) {
                    insert(movie)
                }
            }
        } catch (e: Exception) {
            Log.e("CachingMovieRepository", "Error refreshing movies and genres: ${e.message}")
        }
    }

}
package com.example.androidsideproject.data.entities.movies

import android.util.Log
import com.example.androidsideproject.BuildConfig
import com.example.androidsideproject.model.Movie
import com.example.androidsideproject.network.movie.MovieApiService
import com.example.androidsideproject.network.movie.asDomainObjects
import com.example.androidsideproject.network.movie.getPopularMoviesAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.net.SocketTimeoutException

interface MovieRepository {
    fun getMovies(): Flow<List<Movie>>
    suspend fun insert(item: Movie)
    suspend fun refresh()
}

class CachingMovieRepository(
    private val movieDao: MovieDao,
    private val movieApiService: MovieApiService
) : MovieRepository {

    override fun getMovies(): Flow<List<Movie>> {
        return movieDao.getMovies().map {
            it.asDomainMovies()
        }.onEach {
            if (it.isEmpty()) {
                refresh()
            }
        }
    }

    override suspend fun insert(item: Movie) {
        movieDao.insert(item.getAsMovieDbItem())
    }

    override suspend fun refresh() {
        try {
            movieApiService.getPopularMoviesAsFlow(BuildConfig.API_KEY).collect { movies ->
                for (movie in movies) {
                    insert(movie)
                }
            }
        } catch (e: Exception) {
            Log.e("CachingMovieRepository", "Error refreshing movies: ${e.message}")
        }
    }

}
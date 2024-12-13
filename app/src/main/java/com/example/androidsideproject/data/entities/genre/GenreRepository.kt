package com.example.androidsideproject.data.entities.genre

import android.util.Log
import com.example.androidsideproject.BuildConfig
import com.example.androidsideproject.model.Genre
import com.example.androidsideproject.network.genre.GenreApiService
import com.example.androidsideproject.network.genre.getGenresAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.io.IOException

interface GenreRepository {
    fun getGenres(): Flow<List<Genre>>
    suspend fun insert(item: Genre)
    suspend fun refresh()
}

class CachingGenreRepository(
    private val genreDao: GenreDao,
    private val genreApiService: GenreApiService
) : GenreRepository {

    override fun getGenres(): Flow<List<Genre>> {
        return genreDao.getAllGenres().map { genreDbItems ->
            genreDbItems.map { it.asDomainGenre() }
        }.onEach {
            if (it.isEmpty()) {
                refresh()
            }
        }
    }

    override suspend fun insert(item: Genre) {
        try {
            genreDao.insertGenres(listOf(item.getAsGenreDbItem()))
        } catch (e: Exception) {
            throw IOException("Failed to insert genre: ${item.name}", e)
        }
    }

    override suspend fun refresh() {
        try {
            genreApiService.getGenresAsFlow(BuildConfig.API_KEY).collect { genres ->
                for (genre in genres) {
                    insert(genre)
                }
            }
        } catch (e: Exception) {
            Log.e("CachingGenreRepository", "Error refreshing genres: ${e.message}")
        }
    }
}

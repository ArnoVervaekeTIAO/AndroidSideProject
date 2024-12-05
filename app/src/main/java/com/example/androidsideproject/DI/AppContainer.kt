package com.example.androidsideproject.DI

import android.content.Context
import com.example.androidsideproject.data.database.MainDatabase
import com.example.androidsideproject.data.entities.genre.CachingGenreRepository
import com.example.androidsideproject.data.entities.genre.GenreRepository
import com.example.androidsideproject.data.entities.movies.CachingMovieRepository
import com.example.androidsideproject.data.entities.movies.MovieRepository
import com.example.androidsideproject.network.genre.GenreApiService
import com.example.androidsideproject.network.movie.MovieApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val movieRepository: MovieRepository
    val genreRepository: GenreRepository
}

class DefaultAppContainer(private val applicationContext: Context) : AppContainer {
    private val baseUrl = "https://api.themoviedb.org/3/"

    private val retrofit: Retrofit = Retrofit
        .Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(
            Json {
                ignoreUnknownKeys = true
            }.asConverterFactory("application/json".toMediaType())
        )
        .build()

    private val retrofitMovieService: MovieApiService by lazy {
        retrofit.create(MovieApiService::class.java)
    }

    override val movieRepository: MovieRepository by lazy {
        CachingMovieRepository(
            MainDatabase.getDatabase(context = applicationContext).movieDao(),
            MainDatabase.getDatabase(context = applicationContext).genreDao(),
            retrofitMovieService,
            retrofitGenreService
        )
    }


    private val retrofitGenreService: GenreApiService by lazy {
        retrofit.create(GenreApiService::class.java)
    }

    override val genreRepository: GenreRepository by lazy {
        CachingGenreRepository(
            MainDatabase.getDatabase(context = applicationContext).genreDao(),
            retrofitGenreService
        )
    }
}


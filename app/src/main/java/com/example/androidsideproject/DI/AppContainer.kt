package com.example.androidsideproject.DI

import android.content.Context
import com.example.androidsideproject.data.database.MainDatabase
import com.example.androidsideproject.data.entities.genre.CachingGenreRepository
import com.example.androidsideproject.data.entities.genre.GenreRepository
import com.example.androidsideproject.data.entities.language.CachingLanguageRepository
import com.example.androidsideproject.data.entities.language.LanguageRepository
import com.example.androidsideproject.data.entities.movie.CachingMovieRepository
import com.example.androidsideproject.data.entities.movie.MovieRepository
import com.example.androidsideproject.data.entities.watchlist.WatchlistRepository
import com.example.androidsideproject.network.genre.GenreApiService
import com.example.androidsideproject.network.language.LanguageApiService
import com.example.androidsideproject.network.movie.MovieApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val movieRepository: MovieRepository
    val genreRepository: GenreRepository
    val languageRepository: LanguageRepository
    val watchlistRepository: WatchlistRepository
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

    // MovieApiService instance
    private val retrofitMovieService: MovieApiService by lazy {
        retrofit.create(MovieApiService::class.java)
    }

    // GenreApiService instance
    private val retrofitGenreService: GenreApiService by lazy {
        retrofit.create(GenreApiService::class.java)
    }

    // LanguageApiService instance
    private val retrofitLanguageService: LanguageApiService by lazy {
        retrofit.create(LanguageApiService::class.java)
    }

    // MainDatabase instance
    private val database: MainDatabase by lazy {
        MainDatabase.getDatabase(context = applicationContext)
    }

    // MovieRepository
    override val movieRepository: MovieRepository by lazy {
        CachingMovieRepository(
            database.movieDao(),
            database.genreDao(),
            database.languageDao(),
            retrofitMovieService,
            retrofitGenreService,
            retrofitLanguageService
        )
    }

    // GenreRepository
    override val genreRepository: GenreRepository by lazy {
        CachingGenreRepository(
            database.genreDao(),
            retrofitGenreService
        )
    }

    // LanguageRepository
    override val languageRepository: LanguageRepository by lazy {
        CachingLanguageRepository(
            database.languageDao(),
            retrofitLanguageService
        )
    }

    // WatchlistRepository
    override val watchlistRepository: WatchlistRepository by lazy {
        WatchlistRepository(database.watchlistDao())
    }
}

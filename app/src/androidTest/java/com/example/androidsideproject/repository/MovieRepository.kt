package com.example.androidsideproject.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.androidsideproject.data.database.MainDatabase
import com.example.androidsideproject.model.Movie
import com.example.androidsideproject.api.MockMovieApiService
import com.example.androidsideproject.api.MockGenreApiService
import com.example.androidsideproject.api.MockLanguageApiService
import com.example.androidsideproject.data.entities.genre.GenreDao
import com.example.androidsideproject.data.entities.genre.GenreDbItem
import com.example.androidsideproject.data.entities.language.LanguageDao
import com.example.androidsideproject.data.entities.language.LanguageDbItem
import com.example.androidsideproject.data.entities.movie.CachingMovieRepository
import com.example.androidsideproject.data.entities.movie.MovieDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CachingMovieRepositoryTest {

    private lateinit var database: MainDatabase
    private lateinit var movieDao: MovieDao
    private lateinit var genreDao: GenreDao
    private lateinit var languageDao: LanguageDao
    private lateinit var repository: CachingMovieRepository

    fun insertGenresAndLanguages() = runBlocking {
        val genre1 = GenreDbItem(id = 280, name = "Action")
        val genre2 = GenreDbItem(id = 800, name = "Crime")
        val genre3 = GenreDbItem(id = 180, name = "Drama")
        val genre4 = GenreDbItem(id = 120, name = "Other")
        genreDao.insertGenre(genre1)
        genreDao.insertGenre(genre2)
        genreDao.insertGenre(genre3)
        genreDao.insertGenre(genre4)

        val language1 = LanguageDbItem(id = "en", name = "English")
        languageDao.getLanguageById("en") ?: languageDao.insertLanguage(language1)
    }

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MainDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        movieDao = database.movieDao()
        genreDao = database.genreDao()
        languageDao = database.languageDao()

        val mockMovieApiService = MockMovieApiService()
        val mockGenreApiService = MockGenreApiService()
        val mockLanguageApiService = MockLanguageApiService()

        repository = CachingMovieRepository(
            movieDao,
            genreDao,
            languageDao,
            mockMovieApiService,
            mockGenreApiService,
            mockLanguageApiService
        )

        insertGenresAndLanguages()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertMovieShouldInsertAMovieIntoTheDatabase() = runBlocking {
        // Given
        val movie = Movie(id = 1, title = "The Dark Knight", overview = "Overview1", genreIds = listOf(280, 800, 180), originalLanguage = "en")

        // When
        repository.insert(movie)

        // Then
        val retrievedMovie = movieDao.getMovies().first().first()
        assertEquals(movie.id, retrievedMovie.id)
        assertEquals(movie.title, retrievedMovie.title)
        assertEquals(movie.overview, retrievedMovie.overview)
    }

    @Test
    fun getMoviesShouldRetrieveMoviesFromTheDatabase() = runBlocking {
        // Given
        val movie1 = Movie(id = 1, title = "The Dark Knight", overview = "Overview1", genreIds = listOf(280, 800, 180), originalLanguage = "en")
        val movie2 = Movie(id = 2, title = "Inception", overview = "Overview2", genreIds = listOf(280, 120, 180), originalLanguage = "en")

        // Insert movies into the database
        repository.insert(movie1)
        repository.insert(movie2)

        // When
        val movies = movieDao.getMovies().first()

        // Then
        assertEquals(2, movies.size)
        assertEquals("The Dark Knight", movies[0].title)
        assertEquals("Inception", movies[1].title)
    }

    @Test
    fun refreshShouldPopulateDataWhenDatabaseIsEmpty() = runBlocking {
        val movies = movieDao.getMovies().first()
        assertEquals(0, movies.size)

        // When we call refresh
        repository.refresh()

        // Then
        val refreshedMovies = movieDao.getMovies().first()
        assertEquals(2, refreshedMovies.size)
    }

    @Test
    fun insertMovieShouldCreateCrossRefsInTheDatabase() = runBlocking {
        // Given
        val movie = Movie(id = 1, title = "The Dark Knight", overview = "Overview1", genreIds = listOf(280, 800, 180), originalLanguage = "en")

        // When
        repository.insert(movie)

        // Then
        val genreCrossRefs = movieDao.getAllMovieGenreCrossRefs()
        val languageCrossRefs = movieDao.getAllMovieLanguageCrossRefs()

        assertEquals(3, genreCrossRefs.size)
        assertEquals(1, languageCrossRefs.size)
    }

    @Test
    fun refreshShouldNotInsertDuplicateMovies() = runBlocking {
        // Given
        repository.refresh()

        // When
        val moviesBeforeRefresh = movieDao.getMovies().first()

        // Trigger refresh again
        repository.refresh()

        // Then
        val moviesAfterRefresh = movieDao.getMovies().first()
        assertEquals(moviesBeforeRefresh.size, moviesAfterRefresh.size)
    }
}

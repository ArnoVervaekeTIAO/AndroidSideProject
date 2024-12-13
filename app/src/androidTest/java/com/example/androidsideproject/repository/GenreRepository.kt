import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.androidsideproject.data.database.MainDatabase
import com.example.androidsideproject.data.entities.genre.CachingGenreRepository
import com.example.androidsideproject.data.entities.genre.GenreDao
import com.example.androidsideproject.data.entities.genre.getAsGenreDbItem
import com.example.androidsideproject.model.Genre
import com.example.androidsideproject.api.MockGenreApiService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CachingGenreRepositoryTest {

    private lateinit var database: MainDatabase
    private lateinit var genreDao: GenreDao
    private lateinit var repository: CachingGenreRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MainDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        genreDao = database.genreDao()
        val mockGenreApiService = MockGenreApiService()

        repository = CachingGenreRepository(genreDao, mockGenreApiService)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertGenreShouldInsertAGenreIntoTheDatabase() = runBlocking {
        // Given
        val genre = Genre(id = 1, name = "Action")

        // When
        repository.insert(genre)

        // Then
        val genreDbItem = genre.getAsGenreDbItem()
        val retrievedGenre = genreDao.getGenreById(1)
        assertEquals(genreDbItem, retrievedGenre)
    }

    @Test
    fun getGenresShouldRetrieveGenresFromTheDatabase() = runBlocking {
        // Given
        val genre1 = Genre(id = 1, name = "Action")
        val genre2 = Genre(id = 2, name = "Drama")

        // Inserting genres into the database
        repository.insert(genre1)
        repository.insert(genre2)

        // When
        val genres = genreDao.getAllGenres().first()

        // Then
        assertEquals(2, genres.size)
        assertEquals("Action", genres[0].name)
        assertEquals("Drama", genres[1].name)
    }

    @Test
    fun getGenreByIdShouldReturnCorrectGenre() = runBlocking {
        // Given
        val genre = Genre(id = 1, name = "Action")
        repository.insert(genre)

        // When
        val retrievedGenre = genreDao.getGenreById(1)

        // Then
        assertEquals(genre.id, retrievedGenre?.id)
        assertEquals(genre.name, retrievedGenre?.name)
    }

    @Test
    fun insertGenreShouldNotInsertDuplicateGenres() = runBlocking {
        // Given
        val genre = Genre(id = 1, name = "Action")

        // When
        repository.insert(genre)
        repository.insert(genre)

        // Then
        val genres = genreDao.getAllGenres().first()
        assertEquals(1, genres.size)
    }
}

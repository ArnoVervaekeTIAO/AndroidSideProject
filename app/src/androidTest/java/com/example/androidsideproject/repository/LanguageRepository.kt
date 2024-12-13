package com.example.androidsideproject.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.androidsideproject.data.database.MainDatabase
import com.example.androidsideproject.model.Language
import com.example.androidsideproject.api.MockLanguageApiService
import com.example.androidsideproject.data.entities.language.CachingLanguageRepository
import com.example.androidsideproject.data.entities.language.LanguageDao
import com.example.androidsideproject.data.entities.language.getAsLanguageDbItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CachingLanguageRepositoryTest {

    private lateinit var database: MainDatabase
    private lateinit var languageDao: LanguageDao
    private lateinit var repository: CachingLanguageRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MainDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        languageDao = database.languageDao()
        val mockLanguageApiService = MockLanguageApiService()

        repository = CachingLanguageRepository(languageDao, mockLanguageApiService)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertLanguageShouldInsertALanguageIntoTheDatabase() = runBlocking {
        // Given
        val language = Language(id = "en", name = "English")

        // When
        repository.insert(language)

        // Then
        val languageDbItem = language.getAsLanguageDbItem()
        val retrievedLanguage = languageDao.getLanguageById("en")
        assertEquals(languageDbItem, retrievedLanguage)
    }

    @Test
    fun getLanguagesShouldRetrieveLanguagesFromTheDatabase() = runBlocking {
        // Given
        val language1 = Language(id = "en", name = "English")
        val language2 = Language(id = "fr", name = "French")

        // Inserting languages into the database
        repository.insert(language1)
        repository.insert(language2)

        // When
        val languages = languageDao.getAllLanguages().first()

        // Then
        assertEquals(2, languages.size)
        assertEquals("English", languages[0].name)
        assertEquals("French", languages[1].name)
    }

    @Test
    fun getLanguageByIdShouldReturnCorrectLanguage() = runBlocking {
        // Given
        val language = Language(id = "en", name = "English")
        repository.insert(language)

        // When
        val retrievedLanguage = languageDao.getLanguageById("en")

        // Then
        assertEquals(language.id, retrievedLanguage?.id)
        assertEquals(language.name, retrievedLanguage?.name)
    }

    @Test
    fun insertLanguageShouldNotInsertDuplicateLanguages() = runBlocking {
        // Given
        val language = Language(id = "en", name = "English")

        // When
        repository.insert(language)
        repository.insert(language)

        // Then
        val languages = languageDao.getAllLanguages().first()
        assertEquals(1, languages.size)
    }
}

package com.example.androidsideproject.data.entities.language

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LanguageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguages(languages: List<LanguageDbItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLanguage(language: LanguageDbItem)

    @Query("SELECT * FROM language")
    fun getAllLanguages(): Flow<List<LanguageDbItem>>

    @Query("SELECT * FROM language WHERE id = :languageId LIMIT 1")
    suspend fun getLanguageById(languageId: String): LanguageDbItem?
}
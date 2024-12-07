package com.example.androidsideproject.data.entities.language

import android.util.Log
import com.example.androidsideproject.BuildConfig
import com.example.androidsideproject.model.Language
import com.example.androidsideproject.network.language.LanguageApiService
import com.example.androidsideproject.network.language.getLanguagesAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

interface LanguageRepository {
    fun getLanguages(): Flow<List<Language>>
    suspend fun insert(item: Language)
    suspend fun refresh()
}

class CachingLanguageRepository(
    private val languageDao: LanguageDao,
    private val languageApiService: LanguageApiService
) : LanguageRepository {

    override fun getLanguages(): Flow<List<Language>> {
        return languageDao.getAllLanguages().map { languageDbItems ->
            languageDbItems.map { it.asDomainLanguage() }
        }.onEach {
            if (it.isEmpty()) {
                refresh()
            }
        }
    }

    override suspend fun insert(item: Language) {
        languageDao.insertLanguages(listOf(item.getAsLanguageDbItem()))
    }

    override suspend fun refresh() {
        try {
            languageApiService.getLanguagesAsFlow(BuildConfig.API_KEY).collect { languages ->
                for (language in languages) {
                    insert(language)
                }
            }
        } catch (e: Exception) {
            Log.e("CachingLanguageRepository", "Error refreshing languages: ${e.message}")
        }
    }
}

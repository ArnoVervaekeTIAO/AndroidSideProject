package com.example.androidsideproject.api

import com.example.androidsideproject.model.Language
import com.example.androidsideproject.network.language.ApiLanguage
import com.example.androidsideproject.network.language.LanguageApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockLanguageApiService : LanguageApiService {
    override suspend fun getLanguages(apiKey: String): List<ApiLanguage> {
        return listOf(
            ApiLanguage(id = "en", name = "English"),
            ApiLanguage(id = "fr", name = "French")
        )
    }
}

fun List<ApiLanguage>.asDomainObjects(): List<Language> {
    return map {
        Language(id = it.id, name = it.name)
    }
}

fun LanguageApiService.getLanguagesAsFlow(apiKey: String): Flow<List<Language>> =
    flow {
        val response = getLanguages(apiKey)
        emit(response.asDomainObjects())
    }


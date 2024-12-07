package com.example.androidsideproject.network.language

import com.example.androidsideproject.model.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.GET
import retrofit2.http.Query

interface LanguageApiService {
    @GET("configuration/languages")
    suspend fun getLanguages(
        @Query("api_key") apiKey: String
    ): List<ApiLanguage>
}

fun LanguageApiService.getLanguagesAsFlow(apiKey: String): Flow<List<Language>> =
    flow {
        val response = getLanguages(apiKey)
        emit(response.asDomainObjects())
    }
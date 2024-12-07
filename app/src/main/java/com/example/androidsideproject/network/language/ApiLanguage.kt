package com.example.androidsideproject.network.language

import com.example.androidsideproject.model.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiLanguage(
    @SerialName("iso_639_1") val id: String,
    @SerialName("english_name") val name: String
)

fun List<ApiLanguage>.asDomainObjects(): List<Language> = map {
    Language(
        id = it.id,
        name = it.name,
    )
}

fun ApiLanguage.asDomainObject(): Language = Language(
    id = id,
    name = name,
)

fun Flow<List<ApiLanguage>>.asDomainObjects(): Flow<List<Language>> {
    return this.map {
        it.asDomainObjects()
    }
}

fun Flow<ApiLanguage>.asDomainObject(): Flow<Language> {
    return this.map {
        it.asDomainObject()
    }
}


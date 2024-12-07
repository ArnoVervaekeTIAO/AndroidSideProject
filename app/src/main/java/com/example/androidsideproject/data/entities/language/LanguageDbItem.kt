package com.example.androidsideproject.data.entities.language

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.androidsideproject.model.Language


@Entity(tableName = "language")
data class LanguageDbItem(
    @PrimaryKey
    var id: String,
    var name: String
)

fun Language.getAsLanguageDbItem(): LanguageDbItem = LanguageDbItem(
    id = id,
    name = name,
)


fun LanguageDbItem.asDomainLanguage(): Language = Language(
    id = id,
    name = name,
)

fun List<LanguageDbItem>.asDomainLanguages(): List<Language> {
    return map { it.asDomainLanguage() }
}
package com.example.androidsideproject.data.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidsideproject.data.entities.genre.GenreDao
import com.example.androidsideproject.data.entities.genre.GenreDbItem
import com.example.androidsideproject.data.entities.language.LanguageDao
import com.example.androidsideproject.data.entities.language.LanguageDbItem
import com.example.androidsideproject.data.entities.movie.MovieDao
import com.example.androidsideproject.data.entities.movie.MovieDbItem
import com.example.androidsideproject.data.entities.movie.MovieGenreCrossRef
import com.example.androidsideproject.data.entities.movie.MovieLanguageCrossRef

@Database(
    entities = [MovieDbItem::class, GenreDbItem::class, MovieGenreCrossRef::class, LanguageDbItem::class, MovieLanguageCrossRef::class],
    version = 3,
    exportSchema = false
)
abstract class MainDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun genreDao(): GenreDao
    abstract fun languageDao(): LanguageDao

    companion object {
        @Volatile
        private var INSTANCE: MainDatabase? = null

        fun getDatabase(context: Context): MainDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    MainDatabase::class.java,
                    "androidsideproject_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
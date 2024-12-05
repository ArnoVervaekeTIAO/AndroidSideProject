package com.example.androidsideproject.data.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidsideproject.data.entities.genre.GenreDao
import com.example.androidsideproject.data.entities.genre.GenreDbItem
import com.example.androidsideproject.data.entities.movies.MovieDao
import com.example.androidsideproject.data.entities.movies.MovieDbItem
import com.example.androidsideproject.data.entities.movies.MovieGenreCrossRef


@Database(
    entities = [MovieDbItem::class, GenreDbItem::class, MovieGenreCrossRef::class],
    version = 2,
    exportSchema = false
)
abstract class MainDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun genreDao(): GenreDao

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
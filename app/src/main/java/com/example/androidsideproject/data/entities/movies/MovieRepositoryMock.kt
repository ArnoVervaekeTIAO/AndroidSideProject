package com.example.androidsideproject.data.entities.movies

import com.example.androidsideproject.model.Movie
import kotlinx.coroutines.flow.flow

class MovieRepositoryMock : MovieRepository {
    override fun getMovies() = flow {
        emit(
            listOf(
                Movie(
                    id = 1,
                    title = "Movie 1",
                    genreIds = listOf(1, 2, 3), // Mock genre IDs
                    overview = "This is the overview of Movie 1.", // Mock overview
                    originalLanguage = "English" // Mock original language
                ),
                Movie(
                    id = 2,
                    title = "Movie 2",
                    genreIds = listOf(4, 5), // Mock genre IDs
                    overview = "This is the overview of Movie 2.", // Mock overview
                    originalLanguage = "French" // Mock original language
                ),
                Movie(
                    id = 3,
                    title = "Movie 3",
                    genreIds = listOf(6), // Mock genre IDs
                    overview = "This is the overview of Movie 3.", // Mock overview
                    originalLanguage = "Spanish" // Mock original language
                )
            )
        )
    }

    override suspend fun insert(item: Movie) {}
    override suspend fun refresh() {}
}
package com.example.androidsideproject.data.entities.movies

import com.example.androidsideproject.model.Movie
import kotlinx.coroutines.flow.flow

class MovieRepositoryMock : MovieRepository {
    override fun getMovies() = flow {
        emit(
            listOf(
                Movie(1, "Movie 1"),
                Movie(2, "Movie 2"),
                Movie(3, "Movie 3")
            )
        )
    }

    override suspend fun insert(item: Movie) {}
    override suspend fun refresh() {}
}
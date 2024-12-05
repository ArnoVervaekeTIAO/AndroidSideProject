package com.example.androidsideproject.ui.movie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.androidsideproject.model.MovieWithGenres
import com.example.androidsideproject.ui.theme.MainTheme

class MovieListActivity : ComponentActivity() {

    private val movieViewModel: MovieViewModel by viewModels { MovieViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainTheme {
                MovieListScreen(viewModel = movieViewModel)
            }
        }
    }
}

@Composable
fun MovieListScreen(viewModel: MovieViewModel) {
    val movies by viewModel.movies.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(text = "Movie List", style = MaterialTheme.typography.headlineSmall)

        if (movies.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(movies) { movie ->
                    MovieItem(movie = movie)
                }
            }
        } else {
            Text(text = "Loading movies...")
        }
    }
}


@Composable
fun MovieItem(movie: MovieWithGenres) {
    Column {
        Text(text = "ID: ${movie.id}")
        Text(text = "Title: ${movie.title}")
        Text(text = "Overview: ${movie.overview}")
        Text(text = "Language: ${movie.originalLanguage}")
        Text(text = "Genres: ${movie.genreNames.joinToString(", ")}")
        Button(onClick = { /* Handle movie item click */ }) {
            Text(text = "View Details")
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainTheme {
        MovieListScreen(viewModel = MovieViewModel(MovieRepositoryMock()))
    }
}
 */

package com.example.androidsideproject

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidsideproject.ui.movie.MovieListScreen
import com.example.androidsideproject.ui.movie.MovieViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainApp(
    movieViewModel: MovieViewModel = viewModel(factory = MovieViewModel.Factory),
) {
    MovieListScreen(movieViewModel)
}

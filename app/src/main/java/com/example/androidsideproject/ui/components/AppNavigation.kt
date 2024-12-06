package com.example.androidsideproject.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.androidsideproject.ui.movie.MovieListScreen
import com.example.androidsideproject.ui.movie.MovieViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    movieViewModel: MovieViewModel
) {
    NavHost(navController, startDestination = "browse", modifier = modifier) {
        composable("browse") { MovieListScreen(viewModel = movieViewModel)}
        //composable("watchlist") { Page2() }
        //composable("mymovies") { Page3() }
    }
}
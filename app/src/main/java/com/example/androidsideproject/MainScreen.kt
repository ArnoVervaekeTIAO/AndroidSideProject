package com.example.androidsideproject

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.androidsideproject.ui.components.AppBottomBar
import com.example.androidsideproject.ui.components.AppNavigation
import com.example.androidsideproject.ui.components.AppTopBar
import com.example.androidsideproject.ui.movie.MovieViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainApp(
    movieViewModel: MovieViewModel = viewModel(factory = MovieViewModel.Factory),
) {
    val navController = rememberNavController()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: "browse"

    val pageTitles = mapOf(
        "browse" to stringResource(id = R.string.browse_page_title),
        "watchlist" to stringResource(id = R.string.watchlist_page_title),
        "mymovies" to stringResource(id = R.string.mymovies_page_title)
    )
    val pageTitle = pageTitles[currentRoute] ?: stringResource(id = R.string.movieapp_page_title)

    Scaffold(
        topBar = { AppTopBar(pageTitle = pageTitle) },
        bottomBar = { AppBottomBar(navController) },
    ) { paddingValues ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            movieViewModel = movieViewModel
        )
    }
}



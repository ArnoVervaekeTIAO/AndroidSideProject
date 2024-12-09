package com.example.androidsideproject.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.androidsideproject.ui.MovieListScreen
import com.example.androidsideproject.ui.viewmodel.BrowseViewModel
import com.example.androidsideproject.ui.viewmodel.WatchlistViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    browseViewModel: BrowseViewModel,
    watchlistViewModel: WatchlistViewModel
) {
    val browseNavigationEvent by browseViewModel.navigationEvent.collectAsState(initial = "")
    val watchlistNavigationEvent by watchlistViewModel.navigationEvent.collectAsState(initial = "")

    LaunchedEffect(browseNavigationEvent) {
        if (browseNavigationEvent == "watchlist") {
            navController.navigate("watchlist")
            browseViewModel.resetNavigationEvent()
        }
    }

    LaunchedEffect(watchlistNavigationEvent) {
        if (watchlistNavigationEvent == "watchlist") {
            navController.navigate("watchlist")
            watchlistViewModel.resetNavigationEvent()
        }
    }

    NavHost(navController, startDestination = "browse", modifier = modifier) {
        composable("browse") {
            val uiState = browseViewModel.uiState.collectAsState()
            MovieListScreen(
                allMovies = uiState.value.movies,
                selectedMovies = uiState.value.filteredMovies,
                isLoading = uiState.value.isLoading,
                errorMessage = uiState.value.errorMessage,
                onApplyFilter = { language, genre ->
                    browseViewModel.applyFilter(language, genre)
                },
                onPageChange = { newPage ->
                    browseViewModel.updateSelectedPage(newPage)
                },
                selectedPage = uiState.value.selectedPage,
                addToWatchlist = { movie -> browseViewModel.addMovieToWatchlist(movie) }
            )
        }
        composable("watchlist") {
            val uiState = watchlistViewModel.uiState.collectAsState()
            MovieListScreen(
                allMovies = uiState.value.movies,
                selectedMovies = uiState.value.filteredMovies,
                isLoading = uiState.value.isLoading,
                errorMessage = uiState.value.errorMessage,
                onApplyFilter = { language, genre ->
                    watchlistViewModel.applyFilter(language, genre)
                },
                onPageChange = { newPage ->
                    watchlistViewModel.updateSelectedPage(newPage)
                },
                selectedPage = uiState.value.selectedPage,
                watchlistView = true,
                onRatingChanged = { movie, rating ->
                    watchlistViewModel.updateRating(movie.id.toLong(), rating)
                },
                onDelete = { movie ->
                    watchlistViewModel.deleteMovieFromWatchlist(movie.id.toLong())
                }
            )
        }
    }
}

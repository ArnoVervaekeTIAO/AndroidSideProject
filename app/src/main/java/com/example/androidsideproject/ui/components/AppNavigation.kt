package com.example.androidsideproject.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.androidsideproject.ui.MovieListScreen
import com.example.androidsideproject.ui.viewmodels.BrowseViewModel
import com.example.androidsideproject.ui.viewmodels.WatchlistViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    browseViewModel: BrowseViewModel,
    watchlistViewModel: WatchlistViewModel
) {
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
                selectedPage = uiState.value.selectedPage
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
                selectedPage = uiState.value.selectedPage
            )
        }
    }
}

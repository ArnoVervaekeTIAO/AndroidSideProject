package com.example.androidsideproject.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidsideproject.R
import com.example.androidsideproject.model.MovieView
import com.example.androidsideproject.ui.screen.FilterDialog
import com.example.androidsideproject.ui.screen.MovieCard
import com.example.androidsideproject.ui.state.EmptyView
import com.example.androidsideproject.ui.state.ErrorView
import com.example.androidsideproject.ui.state.LoadingView
import com.example.androidsideproject.ui.theme.MainTheme
import kotlinx.coroutines.launch

class MovieListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val emptyList: List<MovieView> = emptyList()
        setContent {
            MainTheme {
                MovieListScreen(emptyList, emptyList, false, "Preview", { _, _ -> }, {}, 0, {})
            }
        }
    }
}

@Composable
fun MovieListScreen(allMovies: List<MovieView>,
                    selectedMovies: List<MovieView>,
                    isLoading: Boolean,
                    errorMessage: String? = null,
                    onApplyFilter: (String?, String?) -> Unit,
                    onPageChange: (Int) -> Unit,
                    selectedPage: Int,
                    addToWatchlist: (MovieView) -> Unit = {},
                    watchlistView: Boolean = false,
                    onRatingChanged: (MovieView, Int) -> Unit = { _, _ -> },
                    onDelete: (MovieView) -> Unit = {}
){
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    when {
        isLoading -> LoadingView()
        errorMessage != null -> ErrorView(errorMessage = errorMessage)
        selectedMovies.isEmpty() -> EmptyView()
        else -> MovieCarouselWithFilter(
            allMovies = allMovies,
            selectedMovies = selectedMovies,
            isLandscape = isLandscape,
            onApplyFilter = onApplyFilter,
            onPageChange = onPageChange,
            selectedPage = selectedPage,
            addToWatchlist = addToWatchlist,
            watchlistView = watchlistView,
            onRatingChanged = onRatingChanged,
            onDelete = onDelete
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieCarouselWithFilter(
        allMovies: List<MovieView>,
        selectedMovies: List<MovieView>,
        isLandscape: Boolean,
        onApplyFilter: (String?, String?) -> Unit,
        onPageChange: (Int) -> Unit,
        selectedPage: Int,
        addToWatchlist: (MovieView) -> Unit = {},
        watchlistView: Boolean = false,
        onRatingChanged: (MovieView, Int) -> Unit = { _, _ -> },
        onDelete: (MovieView) -> Unit = {}
) {
    var filteredMovies by remember { mutableStateOf(selectedMovies) }
    val pagerState = rememberPagerState(pageCount = { filteredMovies.size }, initialPage = selectedPage)
    val coroutineScope = rememberCoroutineScope()
    var showFilterDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var movieToDelete by remember { mutableStateOf<MovieView?>(null) }


    val updatedOnRatingChanged: (MovieView, Int) -> Unit = { movie, newRating ->
        val updatedMovies = filteredMovies.map { currentMovie ->
            if (currentMovie.id == movie.id) {
                currentMovie.copy(rating = newRating)
            } else {
                currentMovie
            }
        }
        filteredMovies = updatedMovies
        onRatingChanged(movie, newRating)
    }

    LaunchedEffect(pagerState.currentPage) {
        onPageChange(pagerState.currentPage)
    }

    Scaffold(
        topBar = {
            if (!isLandscape) {
                // TopAppBar for portrait mode (with Filter/Delete icon)
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        if (watchlistView)
                        {
                        IconButton(
                            onClick = {
                                if (filteredMovies.isNotEmpty()) {
                                    movieToDelete = filteredMovies[pagerState.currentPage]
                                    showDeleteDialog = true
                                }
                            },
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(id = R.string.delete_movie),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                            }
                    },
                    actions = {
                        IconButton(
                            onClick = { showFilterDialog = true },
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(60.dp)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            Icon(
                                Icons.Filled.FilterList,
                                contentDescription = stringResource(id = R.string.filter_movies),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        val adjustedPadding = if (isLandscape) PaddingValues(0.dp) else paddingValues

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp)
        ) {
            // Filter Icon button for landscape mode - positioned in the top-right corner
            if (isLandscape) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Filter icon (always displayed)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp, end = 16.dp)
                    ) {
                        IconButton(
                            onClick = { showFilterDialog = true },
                            modifier = Modifier
                                .size(60.dp)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            Icon(
                                Icons.Filled.FilterList,
                                contentDescription = stringResource(id = R.string.filter_movies),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    // Delete icon (conditionally displayed)
                    if (watchlistView) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(top = 16.dp, start = 16.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    if (filteredMovies.isNotEmpty()) {
                                        movieToDelete = filteredMovies[pagerState.currentPage]
                                        showDeleteDialog = true
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(id = R.string.delete_movie),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            if (filteredMovies.isNotEmpty()) {
                if (isLandscape) {
                    // Landscape Layout: Buttons next to MovieCard
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    ) {
                        // Previous Button
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    if (pagerState.currentPage > 0) pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            enabled = pagerState.currentPage > 0,
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            Text(text = stringResource(id = R.string.previous))
                        }

                        // Movie Card
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                        ) { pageIndex ->
                            val movie = filteredMovies[pageIndex]
                            MovieCard(movie = movie,
                                isLandscape = true,
                                addToWatchlist = addToWatchlist,
                                watchlistView = watchlistView,
                                onRatingChanged = { newRating ->
                                    updatedOnRatingChanged(movie, newRating)
                                })
                        }

                        // Next Button
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    if (pagerState.currentPage < filteredMovies.size - 1) pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            enabled = pagerState.currentPage < filteredMovies.size - 1,
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            Text(text = stringResource(id = R.string.next))
                        }
                    }
                } else {
                    // Portrait Layout: Buttons below MovieCard
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(adjustedPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.weight(1.3f)
                        ) { pageIndex ->
                            val movie = filteredMovies[pageIndex]
                            MovieCard(movie = movie,
                                isLandscape = false,
                                addToWatchlist = addToWatchlist,
                                watchlistView = watchlistView,
                                onRatingChanged = { newRating ->
                                    updatedOnRatingChanged(movie, newRating)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        if (pagerState.currentPage > 0) pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                },
                                enabled = pagerState.currentPage > 0,
                                modifier = Modifier.padding(horizontal = 16.dp)

                            ) {
                                Text(text = stringResource(id = R.string.previous), style = TextStyle(fontSize = 20.sp))
                            }

                            Text(
                                text = "${pagerState.currentPage + 1} / ${filteredMovies.size}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                            )

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        if (pagerState.currentPage < filteredMovies.size - 1) pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                },
                                enabled = pagerState.currentPage < filteredMovies.size - 1,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Text(text = stringResource(id = R.string.next), style = TextStyle(fontSize = 20.sp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(id = R.string.swipe_hint),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(id = R.string.no_movies_found),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

    if (showDeleteDialog && movieToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(text = stringResource(id = R.string.confirm_delete_title))
            },
            text = {
                Text(text = stringResource(id = R.string.confirm_delete_message))
            },
            confirmButton = {
                Button(
                    onClick = {
                        movieToDelete?.let { onDelete(it) }
                        showDeleteDialog = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.delete))
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }

    if (showFilterDialog) {
        FilterDialog(
            movies = selectedMovies,
            onApplyFilter = { selectedLanguage, selectedGenre ->
                filteredMovies = if (selectedLanguage == null && selectedGenre == null) {
                    allMovies.toList()
                } else {
                    selectedMovies.filter { movie ->
                        (selectedLanguage == null || movie.language == selectedLanguage) &&
                                (selectedGenre == null || selectedGenre in movie.genreNames)
                    }
                }

                onApplyFilter(selectedLanguage, selectedGenre)
                showFilterDialog = false
            },
            onDismiss = {
                showFilterDialog = false
            }
        )
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

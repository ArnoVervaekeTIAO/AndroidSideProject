package com.example.androidsideproject.ui.movie

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidsideproject.R
import com.example.androidsideproject.model.MovieView
import com.example.androidsideproject.ui.theme.MainTheme
import kotlinx.coroutines.launch

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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (movies.isNotEmpty()) {
        MovieCarouselWithFilter(movies = movies, isLandscape = isLandscape)
    } else {
        Text(
            text = stringResource(id = R.string.loading_movies),
            modifier = Modifier.fillMaxSize(),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieCarouselWithFilter(movies: List<MovieView>, isLandscape: Boolean) {
    var filteredMovies by remember { mutableStateOf(movies) }
    val pagerState = rememberPagerState(pageCount = { filteredMovies.size })
    val coroutineScope = rememberCoroutineScope()
    var showFilterDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (!isLandscape) {
                // TopAppBar for portrait mode (with Filter icon)
                TopAppBar(
                    title = { Text("") },
                    actions = {
                        // Manually create a Box to control the icon and circle size
                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)  // Padding applied to the Box (outside of the circle)
                                .size(60.dp)           // Set the size of the circle
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)  // Circle around the icon
                                .padding(12.dp)        // Padding to ensure the icon isn't touching the edge of the circle
                        ) {
                            Icon(
                                Icons.Filled.FilterList,
                                contentDescription = stringResource(id = R.string.filter_movies),
                                modifier = Modifier
                                    .size(36.dp)  // Adjust icon size here (inside the circle)
                                    .align(Alignment.Center)  // Ensure the icon is centered within the circle
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
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 16.dp)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(4.dp)
                ) {
                    IconButton(
                        onClick = { showFilterDialog = true },
                        modifier = Modifier
                    ) {
                        Icon(Icons.Filled.FilterList, contentDescription = stringResource(id = R.string.filter_movies))
                    }
                }
            }

            if (filteredMovies.isNotEmpty()) {
                if (isLandscape) {
                    // Landscape Layout: Buttons next to MovieCard
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
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
                                .padding(horizontal = 16.dp)
                        ) { pageIndex ->
                            val movie = filteredMovies[pageIndex]
                            MovieCard(movie = movie, isLandscape = isLandscape)
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
                            MovieCard(movie = movie, isLandscape = isLandscape)
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

    if (showFilterDialog) {
        FilterDialog(
            movies = movies,
            onApplyFilter = { selectedLanguage, selectedGenre ->
                filteredMovies = movies.filter { movie ->
                    (selectedLanguage == null || movie.language == selectedLanguage) &&
                            (selectedGenre == null || selectedGenre in movie.genreNames)
                }
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }
}

@Composable
fun FilterDialog(
    movies: List<MovieView>,
    onApplyFilter: (String?, String?) -> Unit,
    onDismiss: () -> Unit
) {
    val languages = remember { movies.map { it.language }.distinct() }
    val genres = remember { movies.flatMap { it.genreNames }.distinct() }

    var selectedLanguage by remember { mutableStateOf<String?>(null) }
    var selectedGenre by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { onApplyFilter(selectedLanguage, selectedGenre) }
            ) {
                Text(stringResource(id = R.string.apply))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        text = {
            Column {
                Text(stringResource(id = R.string.filter_movies), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                Text(stringResource(id = R.string.select_language))
                DropdownMenu(selectedValue = selectedLanguage, options = languages, onSelect = { selectedLanguage = it })

                Spacer(modifier = Modifier.height(16.dp))

                Text(stringResource(id = R.string.select_genre))
                DropdownMenu(selectedValue = selectedGenre, options = genres, onSelect = { selectedGenre = it })
            }
        }
    )
}

@Composable
fun DropdownMenu(
    selectedValue: String?,
    options: List<String>,
    onSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(text = selectedValue ?: stringResource(id = R.string.all_options))
        }
        androidx.compose.material3.DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            androidx.compose.material3.DropdownMenuItem(
                onClick = {
                    onSelect(null)
                    expanded = false
                },
                text = { Text(stringResource(id = R.string.all_options)) }
            )
            options.forEach { option ->
                androidx.compose.material3.DropdownMenuItem(
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                    text = { Text(option) }
                )
            }
        }
    }
}

@Composable
fun MovieCard(movie: MovieView, isLandscape: Boolean) {
    val titleFontSize = if (isLandscape) 20.sp else 32.sp
    val bodyFontSize = if (isLandscape) 14.sp else 16.sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = titleFontSize),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "${stringResource(id = R.string.language)} ${movie.language}",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = bodyFontSize),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "${stringResource(id = R.string.genres)} ${movie.genreNames.joinToString(", ")}",
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = bodyFontSize),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = if (movie.overview.length > 400) {
                movie.overview.substring(0, 400) + "..."
            } else {
                movie.overview
            },
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = bodyFontSize),
            modifier = Modifier.padding(bottom = 16.dp),
            maxLines = 6,
            overflow = TextOverflow.Ellipsis
        )
        Button(onClick = { /* Add to watchlist logic */ }) {
            val buttonFontSize = if (isLandscape) 16.sp else 20.sp
            Text(text = stringResource(id = R.string.add_to_watchlist), style = TextStyle(fontSize = buttonFontSize))
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

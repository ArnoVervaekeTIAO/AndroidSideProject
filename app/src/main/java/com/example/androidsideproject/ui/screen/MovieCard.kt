package com.example.androidsideproject.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidsideproject.R
import com.example.androidsideproject.model.MovieView

@Composable
fun MovieCard(
    movie: MovieView,
    isLandscape: Boolean,
    addToWatchlist: (MovieView) -> Unit,
    watchlistView: Boolean,
    onRatingChanged: (Int) -> Unit = {}
) {
    val titleFontSize = if (isLandscape) 20.sp else 32.sp
    val bodyFontSize = if (isLandscape) 14.sp else 16.sp

    var showDialog by remember { mutableStateOf(false) }
    var selectedMovie by remember { mutableStateOf<MovieView?>(null) }

    fun onConfirmAdd() {
        selectedMovie?.let {
            addToWatchlist(it)
        }
        showDialog = false
    }

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
        if (!watchlistView) {
            Button(
                onClick = {
                    selectedMovie = movie
                    showDialog = true
                }
            ) {
                val buttonFontSize = if (isLandscape) 16.sp else 20.sp
                Text(
                    text = stringResource(id = R.string.add_to_watchlist),
                    style = TextStyle(fontSize = buttonFontSize)
                )
            }
        } else {
            RatingStars(
                originalRating = movie.rating ?: 0,
                onRatingChanged = onRatingChanged
            )
        }
    }

    // Confirmation dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = stringResource(id = R.string.confirm))
            },
            text = {
                Text(text = stringResource(id = R.string.add_movie_to_watchlist))
            },
            confirmButton = {
                Button(onClick = { onConfirmAdd() }) {
                    Text(text = stringResource(id = R.string.ok))
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun RatingStars(
    originalRating: Int,
    onRatingChanged: (Int) -> Unit
) {
    var selectedRating by remember { mutableIntStateOf(originalRating) }

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= selectedRating) {
                    Icons.Filled.Star
                } else {
                    Icons.Outlined.Star
                },
                contentDescription = stringResource(id = R.string.star_rating, i),
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        selectedRating = i
                        onRatingChanged(i)
                    },
                tint = if (i <= selectedRating) {
                    Color(0xFFFFE13B)
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

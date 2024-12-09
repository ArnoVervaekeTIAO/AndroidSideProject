package com.example.androidsideproject.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.androidsideproject.R
import com.example.androidsideproject.model.MovieView
import com.example.androidsideproject.ui.screen.DropdownMenu

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
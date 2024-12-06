package com.example.androidsideproject.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun AppBottomBar(navController: NavHostController) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Browse", tint = MaterialTheme.colorScheme.onPrimary) },
            label = {
                Text("Browse", style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onPrimary))
            },
            selected = navController.currentDestination?.route == "browse",
            onClick = { navController.navigate("browse") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Star, contentDescription = "Watchlist", tint = MaterialTheme.colorScheme.onPrimary) },
            label = {
                Text("Watchlist", style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onPrimary))
            },
            selected = navController.currentDestination?.route == "watchlist",
            onClick = { navController.navigate("watchlist") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "My movies", tint = MaterialTheme.colorScheme.onPrimary) },
            label = {
                Text("My movies", style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onPrimary))
            },
            selected = navController.currentDestination?.route == "mymovies",
            onClick = { navController.navigate("mymovies") }
        )
    }
}

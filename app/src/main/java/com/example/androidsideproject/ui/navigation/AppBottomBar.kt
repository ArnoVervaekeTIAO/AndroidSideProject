package com.example.androidsideproject.ui.navigation

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.androidsideproject.R
import com.example.androidsideproject.ui.viewmodel.BrowseViewModel
import com.example.androidsideproject.ui.viewmodel.WatchlistViewModel

@Composable
fun AppNavigationBar(navController: NavHostController,
                     browseViewModel: BrowseViewModel,
                     watchlistViewModel: WatchlistViewModel
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        LandscapeSidebar(navController = navController,
            browseViewModel = browseViewModel,
            watchlistViewModel = watchlistViewModel
            )
    } else {
        AppBottomBar(navController = navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandscapeSidebar(
    navController: NavHostController,
    browseViewModel: BrowseViewModel,
    watchlistViewModel: WatchlistViewModel
) {
    var isMenuOpen by remember { mutableStateOf(false) }
    var currentRoute by remember { mutableStateOf<String?>(null) }

    // Observe navigation changes and update currentRoute
    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentRoute = destination.route
        }
    }

    Box {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                isMenuOpen = !isMenuOpen
                            },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    modifier = Modifier.height(64.dp)
                )
            },
            content = { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    AppNavigation(
                        navController = navController,
                        modifier = Modifier.padding(start = if (isMenuOpen) 200.dp else 0.dp),
                        browseViewModel = browseViewModel,
                        watchlistViewModel = watchlistViewModel
                    )
                }

                if (isMenuOpen) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(175.dp)
                            .align(Alignment.TopStart)
                            .zIndex(1f)
                    ) {
                        Column {
                            IconButton(
                                onClick = { isMenuOpen = false },
                                modifier = Modifier.padding(start = 12.dp, top = 20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Close Menu",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            DrawerContent(navController, currentRoute)
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun DrawerContent(navController: NavHostController, currentRoute: String?) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        NavigationItem(
            icon = Icons.Default.Home,
            label = stringResource(id = R.string.browse),
            selected = currentRoute == "browse",
            onClick = {
                if (currentRoute != "browse") {
                    navController.navigate("browse") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
        NavigationItem(
            icon = Icons.Default.Star,
            label = stringResource(id = R.string.watchlist),
            selected = currentRoute == "watchlist",
            onClick = {
                if (currentRoute != "watchlist") {
                    navController.navigate("watchlist") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
    }
}

@Composable
fun NavigationItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary) },
        label = { Text(label) },
        selected = selected,
        onClick = onClick
    )
}

@Composable
fun AppBottomBar(navController: NavHostController) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Browse", tint = MaterialTheme.colorScheme.onPrimary) },
            label = {
                Text(stringResource(id = R.string.browse), style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onPrimary))
            },
            selected = navController.currentDestination?.route == "browse",
            onClick = { navController.navigate("browse") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Star, contentDescription = "Watchlist", tint = MaterialTheme.colorScheme.onPrimary) },
            label = {
                Text(stringResource(id = R.string.watchlist), style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onPrimary))
            },
            selected = navController.currentDestination?.route == "watchlist",
            onClick = { navController.navigate("watchlist") }
        )
    }
}



package com.example.androidsideproject.navigation

import android.os.Build
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidsideproject.MainApp
import com.example.androidsideproject.ui.viewmodel.BrowseViewModel
import com.example.androidsideproject.ui.viewmodel.WatchlistViewModel
import org.junit.Rule
import org.junit.Test
import androidx.test.filters.SdkSuppress

@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNavigateToWatchlistAndBack() {
        composeTestRule.setContent {
            val browseViewModel = viewModel<BrowseViewModel>(factory = BrowseViewModel.Factory)
            val watchlistViewModel =
                viewModel<WatchlistViewModel>(factory = WatchlistViewModel.Factory)

            MainApp(
                browseViewModel = browseViewModel,
                watchlistViewModel = watchlistViewModel
            )
        }

        composeTestRule.onNodeWithText("Browse Movies").assertIsDisplayed()

        composeTestRule.onNodeWithText("Watchlist").assertIsDisplayed()
        composeTestRule.onNodeWithText("Watchlist").performClick()

        composeTestRule.onNodeWithText("My Watchlist").assertIsDisplayed()
        composeTestRule.onNodeWithText("Browse Movies").assertDoesNotExist()

        composeTestRule.onNodeWithText("Browse").performClick()

        composeTestRule.onNodeWithText("Browse Movies").assertIsDisplayed()
        composeTestRule.onNodeWithText("My Watchlist").assertDoesNotExist()
    }
}

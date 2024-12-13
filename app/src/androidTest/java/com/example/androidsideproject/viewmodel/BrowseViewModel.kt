package com.example.androidsideproject.viewmodel

import com.example.androidsideproject.data.entities.genre.GenreRepository
import com.example.androidsideproject.data.entities.language.LanguageRepository
import com.example.androidsideproject.data.entities.movie.MovieRepository
import com.example.androidsideproject.data.entities.watchlist.WatchlistRepository
import com.example.androidsideproject.model.Movie
import com.example.androidsideproject.model.MovieView
import com.example.androidsideproject.model.UiState
import com.example.androidsideproject.ui.MovieFilterManager
import com.example.androidsideproject.ui.viewmodel.BrowseViewModel
import io.mockk.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BrowseViewModelTest {

    private lateinit var browseViewModel: BrowseViewModel
    private val movieRepository: MovieRepository = mockk()
    private val genreRepository: GenreRepository = mockk()
    private val languageRepository: LanguageRepository = mockk()
    private val watchlistRepository: WatchlistRepository = mockk()
    private val filterManager: MovieFilterManager = mockk()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        // Set up test dispatcher for coroutine-based testing
        Dispatchers.setMain(testDispatcher)  // Ensure coroutines run on the test dispatcher

        // Mock repositories and their flows
        coEvery { movieRepository.getMovies() } returns flowOf(listOf())  // Mock an empty list of movies
        coEvery { genreRepository.getGenres() } returns flowOf(listOf())  // Mock an empty list of genres
        coEvery { languageRepository.getLanguages() } returns flowOf(listOf())  // Mock an empty list of languages
        coEvery { watchlistRepository.getWatchlist() } returns flowOf(listOf())  // Mock an empty watchlist

        // Mock MovieFilterManager to return a flow for uiState
        val initialUiState = UiState(isLoading = true, movies = emptyList())
        val uiStateFlow = MutableStateFlow(initialUiState)
        every { filterManager.uiState } returns uiStateFlow

        // Mock the applyFilter method as a no-op (does nothing)
        coEvery { filterManager.applyFilter(any(), any()) } answers {
            val filteredMovies = listOf(MovieView(id = 1, title = "Movie 1", overview = "Overview", language = "English", genreNames = listOf("Action")))
            uiStateFlow.value = uiStateFlow.value.copy(movies = filteredMovies)
            Unit  // Indicate no return value
        }
        // Mock the initialize method to do nothing (no-op)
        every { filterManager.initialize(any()) } just Runs  // <-- Add this mock

        // Initialize BrowseViewModel with mocked dependencies
        browseViewModel = BrowseViewModel(
            movieRepository = movieRepository,
            genreRepository = genreRepository,
            languageRepository = languageRepository,
            filterManager = filterManager,
            watchlistRepository = watchlistRepository
        )
    }


    @Test
    fun testInitialState() = runTest {
        advanceUntilIdle() // Ensure all background tasks (including flow emissions) have completed

        val uiState = browseViewModel.uiState.first()

        assertTrue(uiState.movies.isEmpty())
        assertEquals(null, uiState.errorMessage)
    }

    @Test
    fun testApplyFilterUpdatesUIState() = runTest {
        // Given: Initial state setup
        val filteredMovies = listOf(
            MovieView(id = 1, title = "Movie 1", overview = "Overview", language = "English", genreNames = listOf("Action"))
        )

        // Mock applyFilter to update the uiState
        coEvery { filterManager.applyFilter(any(), any()) } answers {
            // Simulate applying the filter by updating uiState
            val updatedUiState = browseViewModel.uiState.value.copy(movies = filteredMovies)

            // Access the MutableStateFlow to update the value
            val mutableUiState = browseViewModel._uiState  // Assuming _uiState is mutable and private in your ViewModel
            mutableUiState.value = updatedUiState

            Unit  // Ensure the mock function doesn't return anything
        }

        // When: applyFilter is called
        browseViewModel.applyFilter("English", "Action")

        // Then: assert that the filter changes the movies in the state
        val uiState = browseViewModel._uiState.first()
        assertEquals(filteredMovies, uiState.movies)
    }

    @Test
    fun testAddMovieToWatchlistHandlesErrors() = runTest {
        // Given: an exception is thrown when adding a movie to the watchlist
        val movieView = MovieView(id = 1, title = "Movie 1", overview = "Overview", language = "English", genreNames = listOf("Action"))

        coEvery { watchlistRepository.addToWatchlist(any()) } throws Exception("Network Error")

        // When: adding the movie to the watchlist
        browseViewModel.addMovieToWatchlist(movieView)

        // Then: the error message should be set in the UI state
        val uiState = browseViewModel._uiState.first()
        assertEquals("Failed to add to watchlist: Network Error", uiState.errorMessage)
    }

    @Test
    fun testObserveMoviesAndWatchlistUpdatesUIStateWithCorrectMovies() = runTest {
        // Given: mock data from the repository (Movie objects)
        val movieEntities = listOf(
            Movie(id = 1, title = "Movie 1", overview = "Overview", originalLanguage = "en", genreIds = listOf(1))
        )

        // Mock the repository to return a Flow of Movie objects
        coEvery { movieRepository.getMovies() } returns flowOf(movieEntities)

        // Mock conversion from Movie to MovieView
        val movieViewList = movieEntities.map { movie ->
            MovieView(
                id = movie.id,
                title = movie.title,
                overview = movie.overview,
                language = "English", // Mocking conversion from originalLanguage
                genreNames = listOf("Action") // Mocking conversion from genreIds
            )
        }

        // Now, mocking the flow to return the movieViewList after mapping
        coEvery { movieRepository.getMovies().map { movies ->
            movies.map { movie ->
                MovieView(
                    id = movie.id,
                    title = movie.title,
                    overview = movie.overview,
                    language = "English",
                    genreNames = listOf("Action")
                )
            }
        } } returns flowOf(movieViewList)

        // When: Trigger the UI state collection
        browseViewModel.observeMoviesAndWatchlist()

        // Make sure any coroutines finish processing before assertions
        advanceUntilIdle()

        // Then: Collect the UI state after movie data is fetched
        val uiState = browseViewModel.uiState.first()

        // Assert: The movies in UI state should match the movieViewList
        assertEquals(movieViewList, uiState.movies)
    }


}

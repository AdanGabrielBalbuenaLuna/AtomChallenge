package com.example.atomchallenge

import com.example.atomchallenge.domain.model.Country
import com.example.atomchallenge.domain.usecase.GetCountriesUseCase
import com.example.atomchallenge.presentation.home.HomeUiState
import com.example.atomchallenge.presentation.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getCountriesUseCase: GetCountriesUseCase
    private lateinit var viewModel: HomeViewModel

    private val testCountry = Country(
        name = "Mexico",
        flag = "https://flag.png",
        region = "Americas",
        population = 128000000,
        capital = "Mexico City",
        subregion = "North America",
        languages = listOf("Spanish"),
        currencies = listOf("Mexican peso"),
        timezones = listOf("UTC-06:00")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getCountriesUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given use case returns countries, when viewmodel loads, then state is success`() {
        // GIVEN
        val countries = listOf(testCountry)
        coEvery { getCountriesUseCase() } returns countries

        // WHEN
        viewModel = HomeViewModel(getCountriesUseCase)

        runTest(testDispatcher) {
            advanceUntilIdle()

            // THEN
            val state = viewModel.uiState.value
            assertTrue(state is HomeUiState.Success)
            assertEquals(countries, (state as HomeUiState.Success).countries)
        }
    }

    @Test
    fun `given use case throws exception, when viewmodel loads, then state is error`() {
        // GIVEN
        coEvery { getCountriesUseCase() } throws Exception("Network error")

        // WHEN
        viewModel = HomeViewModel(getCountriesUseCase)

        runTest(testDispatcher) {
            advanceUntilIdle()

            // THEN
            val state = viewModel.uiState.value
            assertTrue(state is HomeUiState.Error)
            assertEquals("Network error", (state as HomeUiState.Error).message)
        }
    }

    @Test
    fun `given countries loaded, when search query changes, then filters correctly`() {
        // GIVEN — usamos países que SÍ contienen "me"
        val countries = listOf(
            testCountry.copy(name = "Mexico"),   // "Me-xico"
            testCountry.copy(name = "Yemen"),    // "Ye-men"
            testCountry.copy(name = "Argentina") // no contiene "me"
        )
        coEvery { getCountriesUseCase() } returns countries

        viewModel = HomeViewModel(getCountriesUseCase)

        runTest(testDispatcher) {
            advanceUntilIdle()

            // WHEN
            viewModel.onSearchQueryChanged("me")

            // THEN — Mexico y Yemen contienen "me"
            val state = viewModel.uiState.value as HomeUiState.Success
            assertEquals(2, state.countries.size)
            assertTrue(state.countries.all {
                it.name.contains("me", ignoreCase = true)
            })
        }
    }

    @Test
    fun `given search query active, when query cleared, then shows all countries`() {
        // GIVEN
        val countries = listOf(
            testCountry.copy(name = "Mexico"),
            testCountry.copy(name = "Argentina")
        )
        coEvery { getCountriesUseCase() } returns countries

        viewModel = HomeViewModel(getCountriesUseCase)

        runTest(testDispatcher) {
            advanceUntilIdle()

            // WHEN — usuario escribe y luego borra
            viewModel.onSearchQueryChanged("mex")
            viewModel.onSearchQueryChanged("")

            // THEN — regresa la lista completa
            val state = viewModel.uiState.value as HomeUiState.Success
            assertEquals(2, state.countries.size)
        }
    }

    @Test
    fun `given error state, when retry, then attempts to load again`() {
        // GIVEN — primero falla, luego funciona
        coEvery { getCountriesUseCase() } throws Exception("Network error") andThen listOf(testCountry)

        viewModel = HomeViewModel(getCountriesUseCase)

        runTest(testDispatcher) {
            advanceUntilIdle()

            // Verificamos que está en error
            assertTrue(viewModel.uiState.value is HomeUiState.Error)

            // WHEN — usuario presiona reintentar
            viewModel.retry()
            advanceUntilIdle()

            // THEN — ahora está en success
            assertTrue(viewModel.uiState.value is HomeUiState.Success)
        }
    }
}

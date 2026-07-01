package com.example.atomchallenge.feature.countries.presentation.home

import app.cash.turbine.test
import com.example.atomchallenge.feature.countries.domain.model.Country
import com.example.atomchallenge.feature.countries.domain.usecase.GetCountriesUseCase
import com.example.atomchallenge.feature.countries.domain.usecase.RefreshCountriesUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getCountriesUseCase: GetCountriesUseCase
    private lateinit var refreshCountriesUseCase: RefreshCountriesUseCase
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
        refreshCountriesUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given use case emits countries, when collected, then state is success`() = runTest {
        // GIVEN
        val countries = listOf(testCountry)
        every { getCountriesUseCase() } returns flowOf(countries)
        coEvery { refreshCountriesUseCase() } returns Unit

        // WHEN
        viewModel = HomeViewModel(getCountriesUseCase, refreshCountriesUseCase)

        // THEN — Turbine se suscribe directo al StateFlow expuesto
        viewModel.uiState.test {
            // 👇 el primer valor real puede tardar un tick por el combine()
            // así que avanzamos hasta encontrar el Success
            var state = awaitItem()
            if (state is HomeUiState.Loading) {
                state = awaitItem()
            }
            assert(state is HomeUiState.Success)
            assert((state as HomeUiState.Success).countries == countries)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given countries loaded, when search query changes, then filters correctly`() = runTest {
        // GIVEN
        val countries = listOf(
            testCountry.copy(name = "Mexico"),
            testCountry.copy(name = "Yemen"),
            testCountry.copy(name = "Argentina")
        )
        every { getCountriesUseCase() } returns flowOf(countries)
        coEvery { refreshCountriesUseCase() } returns Unit

        viewModel = HomeViewModel(getCountriesUseCase, refreshCountriesUseCase)

        viewModel.uiState.test {
            var state = awaitItem()
            if (state is HomeUiState.Loading) state = awaitItem()

            // WHEN
            viewModel.onSearchQueryChanged("me")

            // THEN
            val filtered = awaitItem() as HomeUiState.Success
            assert(filtered.countries.size == 2) // Mexico y Yemen
            assert(filtered.countries.all { it.name.contains("me", ignoreCase = true) })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given refresh fails, when refresh is called, then isRefreshing returns to false`() = runTest {
        every { getCountriesUseCase() } returns flowOf(listOf(testCountry))
        coEvery { refreshCountriesUseCase() } throws Exception("Network error")

        viewModel = HomeViewModel(getCountriesUseCase, refreshCountriesUseCase)

        viewModel.isRefreshing.test {
            assert(awaitItem() == false) // valor inicial

            viewModel.refresh()

            assert(awaitItem() == true)
            assert(awaitItem() == false)

            // 👇 le decimos a Turbine explícitamente: "ya terminé de verificar,
            // cierra el canal sin exigir que consuma más eventos"
            cancelAndIgnoreRemainingEvents()
        }
    }
}
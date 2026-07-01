package com.example.atomchallenge.feature.countries.domain.usecase

import app.cash.turbine.test
import com.example.atomchallenge.feature.countries.domain.model.Country
import com.example.atomchallenge.feature.countries.domain.repository.CountryRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCountriesUseCaseTest {

    private lateinit var repository: CountryRepository
    private lateinit var useCase: GetCountriesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetCountriesUseCase(repository)
    }

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

    @Test
    fun `given repository emits countries, when invoked, then emits same countries sorted`() = runTest {
        // GIVEN — un Flow normal (no suspend), por eso usamos "every" y no "coEvery"
        val unsorted = listOf(
            testCountry.copy(name = "Zimbabwe"),
            testCountry.copy(name = "Argentina")
        )
        every { repository.getCountries() } returns flowOf(unsorted)

        // WHEN + THEN — Turbine se suscribe al Flow resultante
        useCase().test {
            val result = awaitItem()
            assertEquals("Argentina", result[0].name)
            assertEquals("Zimbabwe", result[1].name)
            awaitComplete()
        }
    }

    @Test
    fun `given repository emits empty list, when invoked, then emits empty list`() = runTest {
        every { repository.getCountries() } returns flowOf(emptyList())

        useCase().test {
            val result = awaitItem()
            assertEquals(emptyList<Country>(), result)
            awaitComplete()
        }
    }

    @Test
    fun `given repository emits multiple times, when invoked, then each emission is sorted`() = runTest {
        // GIVEN — simulamos DOS actualizaciones de Room en el tiempo
        // (como cuando refreshCountries() inserta datos nuevos)
        val firstEmission = listOf(testCountry.copy(name = "Mexico"))
        val secondEmission = listOf(
            testCountry.copy(name = "Mexico"),
            testCountry.copy(name = "Argentina")
        )
        every { repository.getCountries() } returns flowOf(firstEmission, secondEmission)

        useCase().test {
            val first = awaitItem()
            assertEquals(1, first.size)

            val second = awaitItem()
            assertEquals("Argentina", second[0].name) // 👈 ordenado en CADA emisión
            assertEquals("Mexico", second[1].name)

            awaitComplete()
        }
    }
}
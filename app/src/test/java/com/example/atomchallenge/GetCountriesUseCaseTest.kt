package com.example.atomchallenge

import com.example.atomchallenge.domain.model.Country
import com.example.atomchallenge.domain.repository.CountryRepository
import com.example.atomchallenge.domain.usecase.GetCountriesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
    fun `given repository returns countries, when invoked, then returns same countries`() {
        // GIVEN
        val expected = listOf(testCountry)
        coEvery { repository.getCountries() } returns expected

        // WHEN - runTest es necesario para correr suspend functions en tests
        runTest {
            val result = useCase()

            // THEN
            assertEquals(expected, result)
        }
    }

    @Test
    fun `given repository returns empty list, when invoked, then returns empty list`() {
        // GIVEN
        coEvery { repository.getCountries() } returns emptyList()

        // WHEN
        runTest {
            val result = useCase()

            // THEN
            assertTrue(result.isEmpty())
        }
    }

    @Test
    fun `given repository returns countries, when invoked, then countries are sorted by name`() {
        // GIVEN — lista desordenada
        val unsorted = listOf(
            testCountry.copy(name = "Zimbabwe"),
            testCountry.copy(name = "Argentina"),
            testCountry.copy(name = "Mexico")
        )
        coEvery { repository.getCountries() } returns unsorted

        // WHEN
        runTest {
            val result = useCase()

            // THEN — deben venir ordenados
            assertEquals("Argentina", result[0].name)
            assertEquals("Mexico", result[1].name)
            assertEquals("Zimbabwe", result[2].name)
        }
    }

    @Test
    fun `given repository throws exception, when invoked, then exception is propagated`() {
        // GIVEN
        coEvery { repository.getCountries() } throws Exception("Network error")

        // WHEN - THEN
        runTest {
            try {
                useCase()
                // Si llega aquí, el test falla — debió lanzar excepción
                assertTrue("Expected exception was not thrown", false)
            } catch (e: Exception) {
                assertEquals("Network error", e.message)
            }
        }
    }
}

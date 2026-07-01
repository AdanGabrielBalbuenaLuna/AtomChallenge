package com.example.atomchallenge.feature.countries.data.repository

import app.cash.turbine.test
import com.example.atomchallenge.feature.countries.data.local.dao.CountryDao
import com.example.atomchallenge.feature.countries.data.local.entity.CountryEntity
import com.example.atomchallenge.feature.countries.data.remote.api.CountryApiService
import com.example.atomchallenge.feature.countries.data.remote.dto.CountryDto
import com.example.atomchallenge.feature.countries.data.remote.dto.FlagsDto
import com.example.atomchallenge.feature.countries.data.remote.dto.NameDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CountryRepositoryImplTest {

    private lateinit var apiService: CountryApiService
    private lateinit var countryDao: CountryDao
    private lateinit var repository: CountryRepositoryImpl

    private val testEntity = CountryEntity(
        name = "Mexico",
        flag = "https://flag.png",
        region = "Americas",
        population = 128000000,
        capital = "Mexico City",
        subregion = "North America",
        languages = "Spanish",
        currencies = "Mexican peso",
        timezones = "UTC-06:00"
    )

    private val testDto = CountryDto(
        name = NameDto(common = "Mexico", official = "United Mexican States"),
        flags = FlagsDto(png = "https://flag.png", svg = null),
        population = 128000000,
        region = "Americas",
        capital = listOf("Mexico City"),
        subregion = "North America",
        languages = mapOf("spa" to "Spanish"),
        currencies = null,
        timezones = listOf("UTC-06:00")
    )

    @Before
    fun setup() {
        apiService = mockk()
        countryDao = mockk()
        repository = CountryRepositoryImpl(apiService, countryDao)
    }

    @Test
    fun `given dao emits entities, when getCountries is called, then emits mapped domain models`() = runTest {
        // GIVEN — el Repository SOLO debe leer de Room, nunca de la red
        every { countryDao.getAllCountries() } returns flowOf(listOf(testEntity))

        // WHEN + THEN
        repository.getCountries().test {
            val result = awaitItem()
            assertEquals("Mexico", result[0].name)
            assertEquals(listOf("Spanish"), result[0].languages)
            awaitComplete()
        }

        // 👇 verificación explícita — la API NUNCA debió ser tocada
        // ❓ Por qué este coVerify es la prueba real del offline-first:
        //    si alguien rompiera la arquitectura y agregara una llamada
        //    a la red dentro de getCountries(), este test fallaría aquí
        coVerify(exactly = 0) { apiService.getCountries() }
    }

    @Test
    fun `given api returns countries, when refreshCountries is called, then dao insertAll is invoked`() = runTest {
        // GIVEN
        coEvery { apiService.getCountries() } returns listOf(testDto)
        coEvery { countryDao.insertAll(any()) } returns Unit

        // WHEN
        repository.refreshCountries()

        // THEN — verificamos que SÍ se llamó insertAll con los datos correctos
        coVerify {
            countryDao.insertAll(match { entities ->
                entities.size == 1 && entities[0].name == "Mexico"
            })
        }
    }

    @Test(expected = Exception::class)
    fun `given api throws exception, when refreshCountries is called, then exception propagates`() = runTest {
        // GIVEN
        coEvery { apiService.getCountries() } throws Exception("Network error")

        // WHEN — debe lanzar la excepción, no tragársela
        // ❓ Por qué: discutimos en el PASO 23 que el Repository
        //    no decide qué hacer con el error — solo lo deja subir
        //    para que el ViewModel decida (silencioso en este caso)
        repository.refreshCountries()
    }

    @Test
    fun `given country exists locally, when getCountryByName is called, then returns from room without calling api`() = runTest {
        // GIVEN
        coEvery { countryDao.getCountryByName("Mexico") } returns testEntity

        // WHEN
        val result = repository.getCountryByName("Mexico")

        // THEN
        assertEquals("Mexico", result?.name)
        coVerify(exactly = 0) { apiService.getCountryByName(any()) }
    }

    @Test
    fun `given country does not exist locally, when getCountryByName is called, then falls back to api`() = runTest {
        // GIVEN — Room no lo tiene (caso del PASO 23: país no sincronizado aún)
        coEvery { countryDao.getCountryByName("Mexico") } returns null
        coEvery { apiService.getCountryByName("Mexico") } returns listOf(testDto)
        coEvery { countryDao.insertAll(any()) } returns Unit

        // WHEN
        val result = repository.getCountryByName("Mexico")

        // THEN
        assertEquals("Mexico", result?.name)
        coVerify { apiService.getCountryByName("Mexico") }
        coVerify { countryDao.insertAll(any()) } // 👈 también lo cachea para la próxima vez
    }
}
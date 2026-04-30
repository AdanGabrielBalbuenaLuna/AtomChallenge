package com.example.atomchallenge

import com.example.atomchallenge.data.remote.dto.CountryDto
import com.example.atomchallenge.data.remote.dto.CurrencyDto
import com.example.atomchallenge.data.remote.dto.FlagsDto
import com.example.atomchallenge.data.remote.dto.NameDto
import com.example.atomchallenge.data.remote.dto.toDomain
import org.junit.Assert.assertEquals
import org.junit.Test

class MapperTest {

    private val testDto = CountryDto(
        name = NameDto(
            common = "Mexico",
            official = "United Mexican States"
        ),
        flags = FlagsDto(
            png = "https://flag.png",
            svg = "https://flag.svg"
        ),
        population = 128000000,
        region = "Americas",
        capital = listOf("Mexico City"),
        subregion = "North America",
        languages = mapOf("spa" to "Spanish"),
        currencies = mapOf("MXN" to CurrencyDto(
            name = "Mexican peso",
            symbol = "$"
        )),
        timezones = listOf("UTC-06:00")
    )

    @Test
    fun `given dto has name, when mapped, then common name is extracted`() {
        // GIVEN
        val dto = testDto

        // WHEN
        val country = dto.toDomain()

        // THEN
        assertEquals("Mexico", country.name)
    }

    @Test
    fun `given dto has flag, when mapped, then png url is extracted`() {
        // GIVEN
        val dto = testDto

        // WHEN
        val country = dto.toDomain()

        // THEN
        assertEquals("https://flag.png", country.flag)
    }

    @Test
    fun `given dto has capital list, when mapped, then first capital is extracted`() {
        // GIVEN
        val dto = testDto

        // WHEN
        val country = dto.toDomain()

        // THEN
        assertEquals("Mexico City", country.capital)
    }

    @Test
    fun `given dto has no capital, when mapped, then capital is NA`() {
        // GIVEN
        val dto = testDto.copy(capital = null)

        // WHEN
        val country = dto.toDomain()

        // THEN
        assertEquals("N/A", country.capital)
    }

    @Test
    fun `given dto has languages, when mapped, then language values are extracted`() {
        // GIVEN
        val dto = testDto

        // WHEN
        val country = dto.toDomain()

        // THEN
        assertEquals(listOf("Spanish"), country.languages)
    }

    @Test
    fun `given dto has no languages, when mapped, then languages is empty list`() {
        // GIVEN
        val dto = testDto.copy(languages = null)

        // WHEN
        val country = dto.toDomain()

        // THEN
        assertEquals(emptyList<String>(), country.languages)
    }

    @Test
    fun `given dto has currencies, when mapped, then currency names are extracted`() {
        // GIVEN
        val dto = testDto

        // WHEN
        val country = dto.toDomain()

        // THEN
        assertEquals(listOf("Mexican peso"), country.currencies)
    }

    @Test
    fun `given dto has no region, when mapped, then region is empty string`() {
        // GIVEN
        val dto = testDto.copy(region = null)

        // WHEN
        val country = dto.toDomain()

        // THEN
        assertEquals("", country.region)
    }
}

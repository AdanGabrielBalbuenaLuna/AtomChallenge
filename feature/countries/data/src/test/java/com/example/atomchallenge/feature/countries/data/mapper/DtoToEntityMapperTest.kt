package com.example.atomchallenge.feature.countries.data.mapper

import com.example.atomchallenge.feature.countries.data.mapper.toEntity
import com.example.atomchallenge.feature.countries.data.remote.dto.CountryDto
import com.example.atomchallenge.feature.countries.data.remote.dto.CurrencyDto
import com.example.atomchallenge.feature.countries.data.remote.dto.FlagsDto
import com.example.atomchallenge.feature.countries.data.remote.dto.NameDto
import org.junit.Assert
import org.junit.Test

class DtoToEntityMapperTest {

    private val testDto = CountryDto(
        name = NameDto(common = "Mexico", official = "United Mexican States"),
        flags = FlagsDto(png = "https://flag.png", svg = "https://flag.svg"),
        population = 128000000,
        region = "Americas",
        capital = listOf("Mexico City"),
        subregion = "North America",
        languages = mapOf("spa" to "Spanish"),
        currencies = mapOf("MXN" to CurrencyDto(name = "Mexican peso", symbol = "$")),
        timezones = listOf("UTC-06:00")
    )

    @Test
    fun `given dto has name, when mapped to entity, then common name is extracted`() {
        val entity = testDto.toEntity()
        Assert.assertEquals("Mexico", entity.name)
    }

    @Test
    fun `given dto has languages map, when mapped to entity, then values are joined with comma`() {
        // GIVEN — dos idiomas, para verificar el join real (no solo uno)
        val dto = testDto.copy(
            languages = mapOf("spa" to "Spanish", "eng" to "English")
        )

        val entity = dto.toEntity()

        // THEN — Room solo entiende String, esto debe venir unido
        Assert.assertEquals("Spanish,English", entity.languages)
    }

    @Test
    fun `given dto has no languages, when mapped to entity, then languages is empty string`() {
        val dto = testDto.copy(languages = null)
        val entity = dto.toEntity()
        Assert.assertEquals("", entity.languages)
    }

    @Test
    fun `given dto has currencies, when mapped to entity, then names are joined with comma`() {
        val dto = testDto.copy(
            currencies = mapOf(
                "MXN" to CurrencyDto(name = "Mexican peso", symbol = "$"),
                "USD" to CurrencyDto(name = "US dollar", symbol = "$")
            )
        )

        val entity = dto.toEntity()

        Assert.assertEquals("Mexican peso,US dollar", entity.currencies)
    }

    @Test
    fun `given dto has no capital, when mapped to entity, then capital is NA`() {
        val dto = testDto.copy(capital = null)
        val entity = dto.toEntity()
        Assert.assertEquals("N/A", entity.capital)
    }

    @Test
    fun `given dto has timezones, when mapped to entity, then they are joined with comma`() {
        val dto = testDto.copy(timezones = listOf("UTC-06:00", "UTC-05:00"))
        val entity = dto.toEntity()
        Assert.assertEquals("UTC-06:00,UTC-05:00", entity.timezones)
    }
}
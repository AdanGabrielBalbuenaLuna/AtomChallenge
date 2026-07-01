package com.example.atomchallenge.feature.countries.data.mapper

import com.example.atomchallenge.feature.countries.data.local.entity.CountryEntity
import com.example.atomchallenge.feature.countries.data.mapper.toDomain
import org.junit.Assert
import org.junit.Test

class EntityToDomainMapperTest {

    private val testEntity = CountryEntity(
        name = "Mexico",
        flag = "https://flag.png",
        region = "Americas",
        population = 128000000,
        capital = "Mexico City",
        subregion = "North America",
        languages = "Spanish,English",
        currencies = "Mexican peso",
        timezones = "UTC-06:00,UTC-05:00"
    )

    @Test
    fun `given entity has comma-separated languages, when mapped to domain, then split into list`() {
        val country = testEntity.toDomain()
        Assert.assertEquals(listOf("Spanish", "English"), country.languages)
    }

    @Test
    fun `given entity has empty languages string, when mapped to domain, then list is empty`() {
        val entity = testEntity.copy(languages = "")
        val country = entity.toDomain()
        Assert.assertEquals(emptyList<String>(), country.languages)
    }

    @Test
    fun `given entity has single timezone, when mapped to domain, then list has one element`() {
        val entity = testEntity.copy(timezones = "UTC-06:00")
        val country = entity.toDomain()
        Assert.assertEquals(listOf("UTC-06:00"), country.timezones)
    }

    @Test
    fun `given entity has multiple timezones, when mapped to domain, then split correctly`() {
        val country = testEntity.toDomain()
        Assert.assertEquals(listOf("UTC-06:00", "UTC-05:00"), country.timezones)
    }

    @Test
    fun `given entity fields, when mapped to domain, then plain fields are copied directly`() {
        val country = testEntity.toDomain()
        Assert.assertEquals("Mexico", country.name)
        Assert.assertEquals("Mexico City", country.capital)
        Assert.assertEquals(128000000, country.population)
    }
}
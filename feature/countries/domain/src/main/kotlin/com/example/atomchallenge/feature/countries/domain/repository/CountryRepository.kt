package com.example.atomchallenge.feature.countries.domain.repository

import com.example.atomchallenge.feature.countries.domain.model.Country
import kotlinx.coroutines.flow.Flow

interface CountryRepository {
    // 👇 Cambiamos List<Country> por Flow<List<Country>>
    // ❓ Por qué: necesitamos que la UI reaccione automáticamente
    //    cuando la cache local cambie (offline-first)
    //    Lo detallamos a fondo cuando lleguemos al Repository real
    fun getCountries(): Flow<List<Country>>
    suspend fun getCountryByName(name: String): Country?
    suspend fun refreshCountries()
}
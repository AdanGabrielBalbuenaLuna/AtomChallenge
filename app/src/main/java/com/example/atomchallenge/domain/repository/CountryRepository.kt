package com.example.atomchallenge.domain.repository

import com.example.atomchallenge.domain.model.Country

interface CountryRepository {
    suspend fun getCountries(): List<Country>
    suspend fun getCountryByName(name: String): Country?
}

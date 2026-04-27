package com.example.atomchallenge.data.repository

import com.example.atomchallenge.data.remote.api.CountryApiService
import com.example.atomchallenge.data.remote.dto.toDomain
import com.example.atomchallenge.domain.model.Country
import com.example.atomchallenge.domain.repository.CountryRepository

class CountryRepositoryImpl(
    private val apiService: CountryApiService
) : CountryRepository {

    override suspend fun getCountries(): List<Country> {
        return apiService.getCountries().map { it.toDomain() }
    }

    override suspend fun getCountryByName(name: String): Country? {
        return apiService.getCountryByName(name)
            .firstOrNull()
            ?.toDomain()
    }
}

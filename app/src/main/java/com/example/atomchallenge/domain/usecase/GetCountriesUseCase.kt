package com.example.atomchallenge.domain.usecase

import com.example.atomchallenge.domain.model.Country
import com.example.atomchallenge.domain.repository.CountryRepository

class GetCountriesUseCase(
    private val repository: CountryRepository
) {
    suspend operator fun invoke(): List<Country> {
        return repository.getCountries()
            .sortedBy { it.name }
    }
}

package com.example.atomchallenge.domain.usecase

import com.example.atomchallenge.domain.model.Country
import com.example.atomchallenge.domain.repository.CountryRepository
import javax.inject.Inject

class GetCountriesUseCase @Inject constructor(
    private val repository: CountryRepository
) {
    suspend operator fun invoke(): List<Country> {
        return repository.getCountries()
            .sortedBy { it.name }
    }
}

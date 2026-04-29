package com.example.atomchallenge.domain.usecase

import com.example.atomchallenge.domain.model.Country
import com.example.atomchallenge.domain.repository.CountryRepository

class GetCountryDetailUseCase(
    private val repository: CountryRepository
) {
    suspend operator fun invoke(name: String): Country? {
        return repository.getCountryByName(name)
    }
}

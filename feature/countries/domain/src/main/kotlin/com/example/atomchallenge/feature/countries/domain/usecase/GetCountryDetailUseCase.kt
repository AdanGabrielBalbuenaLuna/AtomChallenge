package com.example.atomchallenge.feature.countries.domain.usecase

import com.example.atomchallenge.feature.countries.domain.model.Country
import com.example.atomchallenge.feature.countries.domain.repository.CountryRepository
import javax.inject.Inject

class GetCountryDetailUseCase @Inject constructor(
    private val repository: CountryRepository
) {
    suspend operator fun invoke(name: String): Country? {
        return repository.getCountryByName(name)
    }
}
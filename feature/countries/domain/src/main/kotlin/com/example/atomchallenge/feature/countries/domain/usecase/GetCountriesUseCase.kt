package com.example.atomchallenge.feature.countries.domain.usecase

import com.example.atomchallenge.feature.countries.domain.model.Country
import com.example.atomchallenge.feature.countries.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCountriesUseCase @Inject constructor(
    private val repository: CountryRepository
) {
    // 👇 Ya no es suspend operator fun invoke(): List<Country>
    // Ahora retorna un Flow — map() transforma cada emisión, no una sola lista
    operator fun invoke(): Flow<List<Country>> {
        return repository.getCountries()
            .map { countries -> countries.sortedBy { it.name } }
    }
}
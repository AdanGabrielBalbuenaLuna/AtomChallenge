package com.example.atomchallenge.feature.countries.domain.usecase

import com.example.atomchallenge.feature.countries.domain.repository.CountryRepository
import javax.inject.Inject

// 👇 Use Case dedicado para la acción explícita de "refrescar"
// ❓ Por qué uno nuevo y no meterlo en GetCountriesUseCase:
//    son dos intenciones de negocio distintas —
//    "dame lo que tengas" vs "ve a buscar lo más reciente"
class RefreshCountriesUseCase @Inject constructor(
    private val repository: CountryRepository
) {
    suspend operator fun invoke() {
        repository.refreshCountries()
    }
}
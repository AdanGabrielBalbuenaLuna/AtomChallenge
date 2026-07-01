package com.example.atomchallenge.feature.countries.data.mapper

import com.example.atomchallenge.feature.countries.data.local.entity.CountryEntity
import com.example.atomchallenge.feature.countries.domain.model.Country

// 👇 Transforma lo que viene de ROOM en el modelo de DOMAIN
// ❓ Por qué existe: CountryEntity tiene anotaciones de Room y Strings planos
//    Country (domain) es Kotlin puro con List<String> — exactamente
//    lo que la UI y los UseCases ya conocen desde la Parte 1
fun CountryEntity.toDomain(): Country {
    return Country(
        name = this.name,
        flag = this.flag,
        region = this.region,
        population = this.population,
        capital = this.capital,
        subregion = this.subregion,

        // 👇 el camino inverso del mapper anterior
        // "Spanish,English" → split(",") → ["Spanish", "English"]
        // filter para evitar un elemento vacío "" si el string original era ""
        languages = this.languages.split(",").filter { it.isNotBlank() },
        currencies = this.currencies.split(",").filter { it.isNotBlank() },
        timezones = this.timezones.split(",").filter { it.isNotBlank() }
    )
}
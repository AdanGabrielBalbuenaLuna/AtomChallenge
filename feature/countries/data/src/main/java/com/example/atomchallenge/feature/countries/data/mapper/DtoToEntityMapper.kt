package com.example.atomchallenge.feature.countries.data.mapper

import com.example.atomchallenge.feature.countries.data.local.entity.CountryEntity
import com.example.atomchallenge.feature.countries.data.remote.dto.CountryDto

// 👇 Transforma lo que viene de la RED en lo que se guarda en ROOM
// ❓ Por qué existe: CountryDto refleja la API (objetos anidados, nullable)
//    CountryEntity refleja la tabla SQLite (columnas planas, sin nulls)
//    Nunca guardamos el DTO directo en Room — siempre pasa por aquí primero
fun CountryDto.toEntity(): CountryEntity {
    return CountryEntity(
        name = this.name.common,
        flag = this.flags.png,
        region = this.region ?: "",
        population = this.population,
        capital = this.capital?.firstOrNull() ?: "N/A",
        subregion = this.subregion ?: "",

        // 👇 Room no acepta List<String> — la convertimos a un solo String
        // joinToString(",") → ["Spanish", "English"] se vuelve "Spanish,English"
        languages = this.languages?.values?.joinToString(",") ?: "",
        currencies = this.currencies?.values
            ?.mapNotNull { it.name }
            ?.joinToString(",") ?: "",
        timezones = this.timezones?.joinToString(",") ?: ""
    )
}
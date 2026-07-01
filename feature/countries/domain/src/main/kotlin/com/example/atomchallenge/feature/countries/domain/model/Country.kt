package com.example.atomchallenge.feature.countries.domain.model

data class Country(
    val name: String,
    val flag: String,
    val region: String,
    val population: Long,
    val capital: String,
    val subregion: String,
    val languages: List<String>,
    val currencies: List<String>,
    val timezones: List<String>
)
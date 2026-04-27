package com.example.atomchallenge.domain.model

data class Country(
    // HOME DATA
    val name: String,
    val flag: String,
    val region: String,
    val population: Long,

    // DETAIL DATA
    val capital: String,
    val subregion: String,
    val languages: List<String>,
    val currencies: List<String>,
    val timezones: List<String>
)

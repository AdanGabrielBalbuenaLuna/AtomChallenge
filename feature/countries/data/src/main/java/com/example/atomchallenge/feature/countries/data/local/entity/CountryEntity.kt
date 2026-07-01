package com.example.atomchallenge.feature.countries.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// 👇 Representa una fila en la tabla "countries" de Room
// ❓ Por qué no reusar el modelo Country de domain directamente:
//    Room necesita anotaciones (@Entity, @PrimaryKey) que domain
//    no puede tener — domain es Kotlin puro, sin Android/Room
@Entity(tableName = "countries")
data class CountryEntity(
    // 👇 el nombre del país como llave primaria
    // ❓ Por qué: la API de países no tiene un "id" numérico,
    //    pero el nombre es único por país — sirve como llave natural
    @PrimaryKey
    val name: String,
    val flag: String,
    val region: String,
    val population: Long,
    val capital: String,
    val subregion: String,

    // 👇 Room NO soporta List<String> de forma nativa
    // la guardamos como un solo String separado por delimitador
    // y la separamos de vuelta al leerla (lo verás en el mapper)
    val languages: String,   // ej: "Spanish,English"
    val currencies: String,  // ej: "Mexican peso,US dollar"
    val timezones: String    // ej: "UTC-06:00,UTC-05:00"
)
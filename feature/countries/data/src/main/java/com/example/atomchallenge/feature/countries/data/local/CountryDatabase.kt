package com.example.atomchallenge.feature.countries.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.atomchallenge.feature.countries.data.local.dao.CountryDao
import com.example.atomchallenge.feature.countries.data.local.entity.CountryEntity

// 👇 entities = todas las tablas que existen en esta base de datos
// version = 1 porque es la primera versión del schema
@Database(
    entities = [CountryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CountryDatabase : RoomDatabase() {
    // 👇 Room genera automáticamente la implementación de este DAO
    abstract fun countryDao(): CountryDao
}
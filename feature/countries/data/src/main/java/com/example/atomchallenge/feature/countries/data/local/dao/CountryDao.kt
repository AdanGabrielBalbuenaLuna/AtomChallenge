package com.example.atomchallenge.feature.countries.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.atomchallenge.feature.countries.data.local.entity.CountryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {

    // 👇 Flow — Room genera código que emite automáticamente
    // cada vez que la tabla "countries" cambia (insert, update, delete)
    // ❓ Por qué esto es la pieza clave del offline-first:
    //    la UI se suscribe a esto UNA vez y se actualiza sola para siempre,
    //    sin que nadie tenga que "avisarle" manualmente
    @Query("SELECT * FROM countries ORDER BY name ASC")
    fun getAllCountries(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE name = :name LIMIT 1")
    suspend fun getCountryByName(name: String): CountryEntity?

    // 👇 REPLACE — si ya existe un país con ese name (la PK),
    // lo sobrescribe en lugar de fallar por conflicto de llave duplicada
    // ❓ Por qué: cada vez que refrescamos desde la red,
    //    queremos que los datos viejos se actualicen, no que truene
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(countries: List<CountryEntity>)
}
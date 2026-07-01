package com.example.atomchallenge.di

import android.content.Context
import androidx.room.Room
import com.example.atomchallenge.feature.countries.data.local.CountryDatabase
import com.example.atomchallenge.feature.countries.data.local.dao.CountryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "atom_challenge_db"

    @Provides
    @Singleton
    fun provideCountryDatabase(
        // 👇 @ApplicationContext — Hilt sabe inyectar el Context de la app
        // ❓ Por qué no un Context cualquiera: Room necesita un Context
        //    que viva tanto como la app entera, no el de una Activity
        //    que se destruye y recrea — usar el de Activity causaría
        //    memory leaks y crashes al rotar pantalla
        @ApplicationContext context: Context
    ): CountryDatabase {
        return Room.databaseBuilder(
            context,
            CountryDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    // 👇 Hilt necesita "extraer" el DAO desde la Database
    // Room ya generó countryDao() automáticamente (PASO 18)
    // esta función solo expone ese DAO como dependencia inyectable
    @Provides
    @Singleton
    fun provideCountryDao(database: CountryDatabase): CountryDao {
        return database.countryDao()
    }
}
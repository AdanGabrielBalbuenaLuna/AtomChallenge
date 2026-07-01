package com.example.atomchallenge.di

import com.example.atomchallenge.feature.countries.data.local.dao.CountryDao
import com.example.atomchallenge.feature.countries.data.remote.api.CountryApiService
import com.example.atomchallenge.feature.countries.data.repository.CountryRepositoryImpl
import com.example.atomchallenge.feature.countries.domain.repository.CountryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// 👇 Este es el módulo que SOLO puede existir en :app
// porque es el único lugar con visibilidad simultánea de
// domain.CountryRepository y data.CountryRepositoryImpl
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCountryRepository(
        apiService: CountryApiService,
        countryDao: CountryDao
    ): CountryRepository {
        return CountryRepositoryImpl(apiService, countryDao)
    }
}
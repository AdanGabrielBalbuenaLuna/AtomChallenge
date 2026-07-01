package com.example.atomchallenge.di

import com.example.atomchallenge.feature.countries.data.remote.api.CountryApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CountryApiModule {

    @Provides
    @Singleton
    fun provideCountryApiService(retrofit: Retrofit): CountryApiService {
        return retrofit.create(CountryApiService::class.java)
    }
}
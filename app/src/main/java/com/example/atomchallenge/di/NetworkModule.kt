package com.example.atomchallenge.di

import com.example.atomchallenge.data.remote.api.CountryApiService
import com.example.atomchallenge.data.repository.CountryRepositoryImpl
import com.example.atomchallenge.domain.repository.CountryRepository
import com.example.atomchallenge.domain.usecase.GetCountriesUseCase
import com.example.atomchallenge.domain.usecase.GetCountryDetailUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://restcountries.com/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCountryApiService(retrofit: Retrofit): CountryApiService {
        return retrofit.create(CountryApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCountryRepository(
        apiService: CountryApiService
    ): CountryRepository {
        return CountryRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideGetCountriesUseCase(
        repository: CountryRepository
    ): GetCountriesUseCase {
        return GetCountriesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetCountryDetailUseCase(
        repository: CountryRepository
    ): GetCountryDetailUseCase {
        return GetCountryDetailUseCase(repository)
    }
}


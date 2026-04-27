package com.example.atomchallenge.data.remote.api

import com.example.atomchallenge.data.remote.dto.CountryDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CountryApiService {
    @GET("v3.1/all?fields=name,flags,population,region,capital,subregion,languages,currencies,timezones")
    suspend fun getCountries(): List<CountryDto>

    @GET("v3.1/name/{name}")
    suspend fun getCountryByName(@Path("name") name: String): List<CountryDto>
}

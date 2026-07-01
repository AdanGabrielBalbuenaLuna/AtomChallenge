package com.example.atomchallenge.feature.countries.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CountryDto(
    @SerializedName("name") val name: NameDto,
    @SerializedName("flags") val flags: FlagsDto,
    @SerializedName("population") val population: Long,
    @SerializedName("region") val region: String?,
    @SerializedName("capital") val capital: List<String>?,
    @SerializedName("subregion") val subregion: String?,
    @SerializedName("languages") val languages: Map<String, String>?,
    @SerializedName("currencies") val currencies: Map<String, CurrencyDto>?,
    @SerializedName("timezones") val timezones: List<String>?
)

data class NameDto(
    @SerializedName("common") val common: String,
    @SerializedName("official") val official: String
)

data class FlagsDto(
    @SerializedName("png") val png: String,
    @SerializedName("svg") val svg: String?
)

data class CurrencyDto(
    @SerializedName("name") val name: String?,
    @SerializedName("symbol") val symbol: String?
)
package com.example.atomchallenge.feature.countries.presentation.home

import com.example.atomchallenge.feature.countries.domain.model.Country

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val countries: List<Country>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
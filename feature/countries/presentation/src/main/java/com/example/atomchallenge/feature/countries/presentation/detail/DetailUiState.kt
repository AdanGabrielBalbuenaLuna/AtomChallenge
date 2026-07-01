package com.example.atomchallenge.feature.presentation

import com.example.atomchallenge.feature.countries.domain.model.Country

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val country: Country) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}
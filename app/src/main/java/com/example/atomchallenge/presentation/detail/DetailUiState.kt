package com.example.atomchallenge.presentation.detail

import com.example.atomchallenge.domain.model.Country

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val country: Country) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

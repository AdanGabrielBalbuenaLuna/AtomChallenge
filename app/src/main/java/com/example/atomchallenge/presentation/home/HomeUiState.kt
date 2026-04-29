package com.example.atomchallenge.presentation.home

import com.example.atomchallenge.domain.model.Country

sealed class HomeUiState {
    object Loading : HomeUiState()

    data class Success(val countries: List<Country>) : HomeUiState()

    data class Error(val message: String) : HomeUiState()
}

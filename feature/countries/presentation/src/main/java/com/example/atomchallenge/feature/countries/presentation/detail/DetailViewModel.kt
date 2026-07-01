package com.example.atomchallenge.feature.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atomchallenge.feature.countries.domain.usecase.GetCountryDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getCountryDetailUseCase: GetCountryDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadCountry(name: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val country = getCountryDetailUseCase(name)
                if (country != null) {
                    _uiState.value = DetailUiState.Success(country)
                } else {
                    _uiState.value = DetailUiState.Error("Country not found")
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(
                    e.message ?: "Unknown error"
                )
            }
        }
    }
}
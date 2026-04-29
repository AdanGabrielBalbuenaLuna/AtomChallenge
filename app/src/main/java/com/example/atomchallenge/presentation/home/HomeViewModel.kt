package com.example.atomchallenge.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atomchallenge.domain.usecase.GetCountriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCountriesUseCase: GetCountriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadCountries()
    }

    private fun loadCountries() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val countries = getCountriesUseCase()
                _uiState.value = HomeUiState.Success(countries)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    e.message ?: "Error desconocido"
                )
            }
        }
    }
}

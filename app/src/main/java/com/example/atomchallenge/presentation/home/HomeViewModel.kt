package com.example.atomchallenge.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atomchallenge.domain.model.Country
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
    private var allCountries: List<Country> = emptyList()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadCountries()
    }

    private fun loadCountries() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                allCountries = getCountriesUseCase()
                _uiState.value = HomeUiState.Success(allCountries)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        filterCountries(query)
    }

    private fun filterCountries(query: String) {
        if (query.isBlank()) {
            _uiState.value = HomeUiState.Success(allCountries)
            return
        }

        val filtered = allCountries.filter { country ->
            country.name.contains(query, ignoreCase = true)
        }

        _uiState.value = HomeUiState.Success(filtered)
    }

    fun retry() {
        loadCountries()
    }
}
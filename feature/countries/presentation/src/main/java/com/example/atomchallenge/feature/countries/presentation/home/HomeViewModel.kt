package com.example.atomchallenge.feature.countries.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atomchallenge.feature.countries.domain.model.Country
import com.example.atomchallenge.feature.countries.domain.usecase.GetCountriesUseCase
import com.example.atomchallenge.feature.countries.domain.usecase.RefreshCountriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCountriesUseCase: GetCountriesUseCase,
    private val refreshCountriesUseCase: RefreshCountriesUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // 👇 NUEVO — controla el spinner del pull-to-refresh
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // 👇 ESTE es el cambio central de toda la migración
    // ❓ Antes (Parte 1): loadCountries() llamaba al UseCase manualmente
    //    en un viewModelScope.launch, UNA vez, y guardaba el resultado
    //    en _uiState.value = HomeUiState.Success(...)
    //
    // ❓ Ahora: getCountriesUseCase() YA ES un Flow que viene desde Room.
    //    No lo "llamamos" — nos SUSCRIBIMOS a él. Combinamos ese Flow
    //    con el Flow del searchQuery para que el filtro de búsqueda
    //    reaccione en tiempo real sin volver a tocar la base de datos
    val uiState: StateFlow<HomeUiState> = combine(
        getCountriesUseCase(),
        searchQuery
    ) { countries, query ->
        filterCountries(countries, query)
    }
        .map<List<Country>, HomeUiState> { filtered -> HomeUiState.Success(filtered) }
        .catch { e ->
            // 👇 si Room fallara (poco común) o el mapeo lanzara excepción
            emit(HomeUiState.Error(e.message ?: "Unknown error"))
        }
        .stateIn(
            scope = viewModelScope,
            // 👇 SharingStarted.WhileSubscribed — el Flow de Room solo
            // se mantiene activo mientras HAY alguien observando (la UI visible)
            // se detiene solo si la pantalla se destruye, ahorra recursos
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading
        )

    init {
        // 👇 al abrir la pantalla, intenta traer datos frescos en silencio
        // si falla (sin internet), el Flow de Room sigue funcionando
        // con lo que ya había — la UI nunca lo nota como un "error visible"
        refresh()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    // 👇 esta función la usa tanto init{} como el pull-to-refresh manual
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                refreshCountriesUseCase()
            } catch (e: Exception) {
                // 👇 silencioso a propósito: si falla el refresh,
                // Room sigue teniendo los datos viejos — eso ES el offline-first
                // (no rompemos la UI por un fallo de red en background)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun filterCountries(countries: List<Country>, query: String): List<Country> {
        if (query.isBlank()) return countries
        return countries.filter { it.name.contains(query, ignoreCase = true) }
    }
}
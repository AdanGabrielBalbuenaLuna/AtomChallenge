package com.example.atomchallenge.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.atomchallenge.domain.model.Country

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    countryName: String,
    onNavigateBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    LaunchedEffect(countryName) {
        viewModel.loadCountry(countryName)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del País") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val state = uiState) {
                is DetailUiState.Loading -> LoadingContent()
                is DetailUiState.Success -> DetailContent(country = state.country)
                is DetailUiState.Error -> ErrorContent(message = state.message)
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Error: $message",
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun DetailContent(country: Country) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = country.flag,
            contentDescription = "Bandera de ${country.name}",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Text(
            text = country.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        InfoCard(title = "Capital", value = country.capital)
        InfoCard(title = "Región", value = country.region)
        InfoCard(title = "Subregión", value = country.subregion)
        InfoCard(
            title = "Población",
            value = "%,d".format(country.population)
        )
        InfoCard(
            title = "Idiomas",
            value = country.languages.joinToString(", ")
        )
        InfoCard(
            title = "Monedas",
            value = country.currencies.joinToString(", ")
        )
        InfoCard(
            title = "Zonas horarias",
            value = country.timezones.joinToString(", ")
        )
    }
}

@Composable
private fun InfoCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value.ifEmpty { "N/A" },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

package com.example.atomchallenge.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onCountryClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is HomeUiState.Loading -> LoadingContent()
        is HomeUiState.Success -> CountryListContent(
            countries = state.countries,
            onCountryClick = onCountryClick
        )
        is HomeUiState.Error -> ErrorContent(message = state.message)
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
private fun CountryListContent(
    countries: List<Country>,
    onCountryClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = countries,
            key = { it.name }
        ) { country ->
            CountryItem(
                country = country,
                onClick = { onCountryClick(country.name) }
            )
        }
    }
}

@Composable
private fun CountryItem(
    country: Country,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = country.flag,
                contentDescription = "Bandera de ${country.name}",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            Column {
                Text(
                    text = country.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = country.region,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Población: ${"%,d".format(country.population)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

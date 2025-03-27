package com.example.pokemon.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pokemon.Event
import com.example.pokemon.ui.PokemonImage

@Composable
fun DetailRoute(
    onBackClick: () -> Unit,
    navigateToDetail: (pokemonId: Long) -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val detailUiState by viewModel.detail.collectAsStateWithLifecycle()
    DetailScreen(
        detailUiState = detailUiState,
        errorMessage = viewModel.errorMessage,
        onBackClick = onBackClick,
        navigateToDetail = navigateToDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScreen(
    detailUiState: DetailUiState,
    errorMessage: Event<String>?,
    onBackClick: () -> Unit,
    navigateToDetail: (pokemonId: Long) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    Text(
                        modifier = Modifier.padding(end = 16.dp),
                        text = "#${detailUiState.id}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    ) { innerPadding ->
        DetailContent(
            modifier = Modifier.padding(innerPadding),
            name = detailUiState.name,
            image = detailUiState.image,
            types = detailUiState.types,
            evolvesFrom = detailUiState.evolvesFrom,
            description = detailUiState.description,
            evolvesFromClicked = {
                navigateToDetail(it)
            }
        )
    }
    LaunchedEffect(errorMessage) {
        if (errorMessage == null) return@LaunchedEffect
        snackbarHostState.showSnackbar(message = errorMessage.peekContent())
    }
}

@Composable
private fun DetailContent(
    modifier: Modifier = Modifier,
    name: String,
    image: String,
    types: List<String>,
    evolvesFrom: EvolvesFrom?,
    description: String,
    evolvesFromClicked: (pokemonId: Long) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PokemonImage(
            modifier = Modifier.size(200.dp),
            image = image
        )
        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            types.forEach {
                Text(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    text = it,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        evolvesFrom?.let {
            EvolvesFromItem(
                name = it.name,
                image = it.image,
                onClick = { evolvesFromClicked(it.id) }
            )
        }
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            text = description,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun EvolvesFromItem(name: String, image: String, onClick: () -> Unit) {
    Surface(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Evolves from",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(text = name, style = MaterialTheme.typography.bodyLarge)
            }
            PokemonImage(
                modifier = Modifier.size(60.dp),
                image = image
            )
        }
    }
}

@Composable
@Preview
private fun DetailScreenPreview() {
    DetailScreen(
        detailUiState = DetailUiState(
            0,
            "pikachu",
            "",
            listOf("water", "fire"),
            EvolvesFrom(1, "charmander", ""),
            "description"
        ),
        errorMessage = null,
        onBackClick = {},
        navigateToDetail = {}
    )
}

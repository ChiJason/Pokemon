package com.example.pokemon.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pokemon.R
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
        onBackClick = onBackClick,
        navigateToDetail = navigateToDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    detailUiState: DetailUiState,
    onBackClick: () -> Unit,
    navigateToDetail: (pokemonId: Long) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.displayCutout),
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    if (detailUiState is DetailUiState.Success) {
                        Text(
                            modifier = Modifier.padding(end = 16.dp),
                            text = "#${detailUiState.data.id}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        when (detailUiState) {
            DetailUiState.Loading -> {
                Box(
                    modifier = modifier.semantics { contentDescription = "loading" },
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is DetailUiState.Success -> {
                val data = detailUiState.data
                DetailContent(
                    modifier = modifier,
                    name = data.name,
                    image = data.image,
                    types = data.types,
                    evolvesFrom = data.evolvesFrom,
                    description = data.description,
                    evolvesFromClicked = {
                        navigateToDetail(it)
                    }
                )
            }

            is DetailUiState.Error -> {
                Box(modifier = modifier, contentAlignment = Alignment.Center) {
                    Text(
                        text = detailUiState.errorMessage,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
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
            .windowInsetsPadding(WindowInsets.displayCutout)
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState()),
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
                    text = stringResource(R.string.evolves_from),
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
@Preview(device = "id:pixel_9_pro_xl")
@Preview(device = "spec:parent=pixel_9_pro_xl,orientation=landscape")
private fun DetailScreenPreview() {
    DetailScreen(
        detailUiState = DetailUiState.Success(
            data = DetailData(
                0,
                "pikachu",
                "",
                listOf("water", "fire"),
                EvolvesFrom(1, "charmander", ""),
                "description"
            )
        ),
        onBackClick = {},
        navigateToDetail = {}
    )
}

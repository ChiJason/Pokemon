package com.example.pokemon.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pokemon.Event
import com.example.pokemon.R
import com.example.pokemon.ui.PokemonImage

@Composable
fun HomeScreenRoute(
    navigateToDetail: (pokemonId: Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val screenUiState by viewModel.collections.collectAsStateWithLifecycle()
    val pocketItems by viewModel.pocketItems.collectAsStateWithLifecycle()
    HomeScreen(
        collections = screenUiState,
        pocketItems = pocketItems,
        errorMessage = viewModel.errorMessage,
        navigateToDetail = navigateToDetail,
        capturePokemon = viewModel::capturePokemon,
        releasePokemon = viewModel::releasePokemon
    )
}

@Composable
private fun HomeScreen(
    collections: List<PokemonTypeItemUiState>,
    pocketItems: List<PocketItemUiState>,
    errorMessage: Event<String>?,
    navigateToDetail: (pokemonId: Long) -> Unit,
    capturePokemon: (item: PokemonItemUiState) -> Unit,
    releasePokemon: (item: PocketItemUiState) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            PocketItem(
                pocketItems = pocketItems,
                onItemClick = navigateToDetail,
                releasePokemon = releasePokemon
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(collections, key = { it.type }) {
                PokemonTypeItem(
                    type = it.type,
                    pokemonItems = it.pokemonItems,
                    onBallClick = { id ->
                        val item = it.pokemonItems.find { it.id == id }
                        item?.run { capturePokemon(this) }
                    },
                    onItemClick = navigateToDetail
                )
            }
        }
        LaunchedEffect(errorMessage) {
            if (errorMessage == null) return@LaunchedEffect
            snackbarHostState.showSnackbar(message = errorMessage.peekContent())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PocketItem(
    pocketItems: List<PocketItemUiState>,
    onItemClick: (pokemonId: Long) -> Unit,
    releasePokemon: (item: PocketItemUiState) -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(TopAppBarDefaults.windowInsets)
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionItem(title = "Pocket", tail = "${pocketItems.size}")
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(pocketItems, key = { it.id }) {
                PokemonItem(
                    name = it.name,
                    image = it.image,
                    onItemClick = { onItemClick(it.pokemonId) },
                    onBallClick = { releasePokemon(it) }
                )
            }
        }
        HorizontalDivider(thickness = 2.dp, color = Color.Blue)
    }
}

@Composable
private fun PokemonTypeItem(
    type: String,
    pokemonItems: List<PokemonItemUiState>,
    onBallClick: (pokemonId: Long) -> Unit,
    onItemClick: (pokemonId: Long) -> Unit
) {
    Column(
        modifier = Modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionItem(title = type, tail = "${pokemonItems.size}")
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(pokemonItems, key = { it.id }) {
                PokemonItem(
                    it.name, it.image,
                    onItemClick = { onItemClick(it.id) },
                    onBallClick = { onBallClick(it.id) }
                )
            }
        }
    }
}

@Composable
private fun SectionItem(title: String, tail: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = tail,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun PokemonItem(
    name: String,
    image: String,
    onItemClick: () -> Unit,
    onBallClick: () -> Unit
) {
    Card(
        onClick = onItemClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                PokemonImage(
                    modifier = Modifier.size(120.dp),
                    image = image
                )
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    onClick = onBallClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_catching_pokemon_24),
                        contentDescription = "ball",
                        tint = Color.Unspecified
                    )
                }
            }
            Text(text = name)
        }
    }
}

@Composable
@Preview
private fun HomeScreenPreview() {
    HomeScreen(
        collections = listOf(
            PokemonTypeItemUiState(
                "Fire",
                listOf(
                    PokemonItemUiState(1, "Bulbasaur", ""),
                    PokemonItemUiState(2, "Ivysaur", "")
                )
            ),
            PokemonTypeItemUiState(
                "Water",
                listOf(
                    PokemonItemUiState(1, "Bulbasaur", ""),
                    PokemonItemUiState(2, "Ivysaur", "")
                )
            )
        ),
        pocketItems = listOf(
            PocketItemUiState(0, 0, "Bulbasaur", ""),
            PocketItemUiState(1, 0, "Bulbasaur", ""),
            PocketItemUiState(2, 0, "Bulbasaur", ""),
        ),
        errorMessage = null,
        navigateToDetail = {},
        capturePokemon = {},
        releasePokemon = {}
    )
}

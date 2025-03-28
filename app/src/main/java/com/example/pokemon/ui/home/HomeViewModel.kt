package com.example.pokemon.ui.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon.Event
import com.example.pokemon.data.PokemonRepository
import com.example.pokemon.data.db.CapturedPokemon
import com.example.pokemon.data.db.TypeWithPokemons
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: PokemonRepository
) : ViewModel() {

    var errorMessage by mutableStateOf<Event<String>?>(null)
        private set

    var scrollToFirst by mutableStateOf<Event<Unit>?>(null)
        private set

    val collections = repo.getTypesWithPokemons().map {
        it.toCollectionItemUiState()
    }.catch {
        errorMessage = Event(it.message.orEmpty())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val pocketItems = repo.getCapturedPokemons().map {
        it.toPocketItemUiState()
    }.catch {
        errorMessage = Event(it.message.orEmpty())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        fetchPrepopulatedData()
    }

    fun fetchPrepopulatedData() {
        viewModelScope.launch {
            runCatching {
                repo.fetchPokemonList()
            }.onFailure {
                Log.e("HomeViewModel", "fetchPrepopulatedData: $it")
                val error = if (it is UnknownHostException) "Connection Failed" else it.message.orEmpty()
                errorMessage = Event(error)
            }
        }
    }

    fun capturePokemon(pokemonId: Long) {
        viewModelScope.launch {
            runCatching {
                repo.capturePokemon(pokemonId)
                scrollToFirst = Event(Unit)
            }.onFailure {
                errorMessage = Event("capture pokemon failed")
            }
        }
    }

    fun releasePokemon(pocketId: Long) {
        viewModelScope.launch {
            runCatching {
                repo.releasePokemon(pocketId)
            }.onFailure {
                errorMessage = Event("release pokemon failed")
            }
        }
    }
}

private fun List<TypeWithPokemons>.toCollectionItemUiState(): List<PokemonCollectionItemUiState> =
    map {
        PokemonCollectionItemUiState(
            type = it.type.typeName,
            pokemonItems = it.pokemons.map { pokemon ->
                PokemonItemUiState(
                    id = pokemon.pokemonId,
                    name = pokemon.name,
                    image = pokemon.image
                )
            }
        )
    }

private fun List<CapturedPokemon>.toPocketItemUiState(): List<PocketItemUiState> = map {
    PocketItemUiState(
        id = it.id,
        pokemonId = it.pokemonId,
        name = it.name,
        image = it.image
    )
}

data class PokemonCollectionItemUiState(
    val type: String,
    val pokemonItems: List<PokemonItemUiState>
)

data class PokemonItemUiState(
    val id: Long,
    val name: String,
    val image: String
)

data class PocketItemUiState(
    val id: Long,
    val pokemonId: Long,
    val name: String,
    val image: String
)

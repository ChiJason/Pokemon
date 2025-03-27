package com.example.pokemon.ui.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon.Event
import com.example.pokemon.data.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: PokemonRepository
) : ViewModel() {

    var errorMessage by mutableStateOf<Event<String>?>(null)
        private set

    val collections = repo.fakeStorage.map { storage ->
        storage.map { (type, items) ->
            PokemonTypeItemUiState(
                type,
                items.map {
                    PokemonItemUiState(
                        it.id,
                        it.name,
                        it.image
                    )
                }
            )
        }
    }.catch {
        errorMessage = Event(it.message.orEmpty())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _pocketItems = MutableStateFlow<List<PocketItemUiState>>(emptyList())
    val pocketItems = _pocketItems.asStateFlow()

    init {
        fetchPrepopulatedData()
    }

    fun fetchPrepopulatedData() {
        viewModelScope.launch {
            runCatching {
                repo.fetchPokemonList()
            }.onFailure { error ->
                Log.e("HomeViewModel", "fetchPrepopulatedData: $error")
                errorMessage = Event(error.message.orEmpty())
            }
        }
    }

    fun capturePokemon(item: PokemonItemUiState) {
        _pocketItems.update {
            pocketItems.value + PocketItemUiState(
                pocketItems.value.size.toLong(),
                item.id,
                item.name,
                item.image
            )
        }
    }

    fun releasePokemon(item: PocketItemUiState) {
        _pocketItems.update { pocketItems.value - item }
    }
}

data class PokemonTypeItemUiState(
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

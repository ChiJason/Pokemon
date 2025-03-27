package com.example.pokemon.ui.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.pokemon.Event
import com.example.pokemon.data.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repo: PokemonRepository
) : ViewModel() {

    private val pokemonId: Long = savedStateHandle.toRoute<Detail>().pokemonId

    var errorMessage by mutableStateOf<Event<String>?>(null)
        private set

    val detail = repo.getPokemonDetail(pokemonId)
        .catch { error ->
            errorMessage = Event(error.message.orEmpty())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DetailUiState()
        )
}

data class DetailUiState(
    val id: Long = 0,
    val name: String = "",
    val image: String = "",
    val types: List<String> = emptyList(),
    val evolvesFrom: EvolvesFrom? = null,
    val description: String = ""
)

data class EvolvesFrom(
    val id: Long,
    val name: String,
    val image: String
)

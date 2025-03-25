package com.example.pokemon.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.util.CoilUtils.result
import com.example.pokemon.Event
import com.example.pokemon.data.network.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: PokemonRepository
) : ViewModel() {

    private val _screenUiState = MutableStateFlow(HomeScreenUiState())
    val screenUiState = _screenUiState.asStateFlow()

    init {
        repo.getAllPokemon().onEach { pokemonList ->
            _screenUiState.update { it.copy(data = pokemonList) }
        }.catch { error ->
            _screenUiState.update { it.copy(error = Event(error.message.orEmpty())) }
        }.launchIn(viewModelScope)
    }

    fun fetchPrepopulatedData() {
        viewModelScope.launch {
            runCatching {
                repo.fetchPrepopulatedData()
            }.onFailure { error ->
                _screenUiState.update { it.copy(error = Event(error.message.orEmpty())) }
            }
        }
    }
}

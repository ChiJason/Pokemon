package com.example.pokemon.ui.home

import com.example.pokemon.Event
import com.example.pokemon.data.db.Pokemon

data class HomeScreenUiState(
    val data: List<Pokemon> = emptyList(),
    val error: Event<String>? = null,
)

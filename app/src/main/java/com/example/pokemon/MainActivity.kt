package com.example.pokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pokemon.ui.home.HomeScreen
import com.example.pokemon.ui.home.HomeViewModel
import androidx.compose.runtime.getValue
import com.example.pokemon.ui.theme.PokemonTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val screenUiState by viewModel.screenUiState.collectAsStateWithLifecycle()
            PokemonTheme {
                HomeScreen(screenUiState = screenUiState)
            }
        }
        viewModel.fetchPrepopulatedData()
    }
}

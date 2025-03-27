package com.example.pokemon.ui.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object Home

fun NavGraphBuilder.homeScreen(navigateToDetail: (pokemonId: Long) -> Unit) {
    composable<Home> {
        HomeScreenRoute(
            navigateToDetail = navigateToDetail
        )
    }
}

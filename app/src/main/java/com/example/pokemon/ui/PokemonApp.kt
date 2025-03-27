package com.example.pokemon.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.pokemon.ui.detail.detailScreen
import com.example.pokemon.ui.detail.navigateToDetail
import com.example.pokemon.ui.home.Home
import com.example.pokemon.ui.home.homeScreen

@Composable
fun PokemonApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Home,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        homeScreen(navigateToDetail = navController::navigateToDetail)
        detailScreen(
            onBackClick = navController::popBackStack,
            navigateToDetail = navController::navigateToDetail
        )
    }
}

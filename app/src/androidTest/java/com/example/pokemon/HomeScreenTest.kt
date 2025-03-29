package com.example.pokemon

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.pokemon.ui.home.HomeScreen
import com.example.pokemon.ui.home.PocketItemUiState
import com.example.pokemon.ui.home.PokemonCollectionItemUiState
import com.example.pokemon.ui.home.PokemonItemUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun should_display_collections_item_when_data_is_loaded() {
        composeTestRule.setContent {
            HomeScreen(
                collections = listOf(
                    PokemonCollectionItemUiState(
                        "Fire",
                        listOf(
                            PokemonItemUiState(1, "Bulbasaur", ""),
                            PokemonItemUiState(2, "Ivysaur", "")
                        )
                    )
                ),
                pocketItems = listOf(
                    PocketItemUiState(0, 0, "Pikachu", ""),
                    PocketItemUiState(1, 1, "Butterfree", "")
                ),
                scrollToFirst = null,
                errorMessage = null,
                navigateToDetail = {},
                capturePokemon = {},
                releasePokemon = {}
            )
        }
        with(composeTestRule) {
            onNodeWithText("Pocket").assertExists()
            onNodeWithText("Pikachu").assertExists()
            onNodeWithText("Butterfree").assertExists()
            onNodeWithText("Fire").assertExists()
            onNodeWithText("Bulbasaur").assertExists()
            onNodeWithText("Ivysaur").assertExists()
            onAllNodesWithText("2").assertCountEquals(2)
        }
    }

    @Test
    fun should_handle_click_event_when_item_is_clicked() {
        composeTestRule.setContent {
            HomeScreen(
                collections = listOf(
                    PokemonCollectionItemUiState(
                        "Fire",
                        listOf(
                            PokemonItemUiState(1, "Bulbasaur", ""),
                            PokemonItemUiState(2, "Ivysaur", "")
                        )
                    )
                ),
                pocketItems = listOf(
                    PocketItemUiState(0, 0, "Pikachu", ""),
                    PocketItemUiState(1, 1, "Butterfree", "")
                ),
                scrollToFirst = null,
                errorMessage = null,
                navigateToDetail = { id ->
                    assertEquals(0, id)
                },
                capturePokemon = { pokemonId ->
                    assertEquals(2, pokemonId)
                },
                releasePokemon = { pocketId ->
                    assertEquals(1, pocketId)
                }
            )
        }
        with(composeTestRule) {
            onNodeWithText("Pikachu").assertExists().performClick()
            onNodeWithText("Butterfree")
                .onChildren()
                .filterToOne(hasContentDescription("ball"))
                .performClick()
            onNodeWithText("Ivysaur")
                .onChildren()
                .filterToOne(hasContentDescription("ball"))
                .performClick()
        }
    }
}

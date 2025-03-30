package com.example.pokemon

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.pokemon.ui.detail.DetailData
import com.example.pokemon.ui.detail.DetailScreen
import com.example.pokemon.ui.detail.DetailUiState
import com.example.pokemon.ui.detail.EvolvesFrom
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun should_display_loading_when_state_is_loading() {
        composeTestRule.setContent {
            DetailScreen(
                detailUiState = DetailUiState.Loading,
                onBackClick = {},
                navigateToDetail = {}
            )
        }
        with(composeTestRule) {
            onNodeWithContentDescription("loading").assertExists()
        }
    }

    @Test
    fun should_display_error_message_when_state_is_error() {
        composeTestRule.setContent {
            DetailScreen(
                detailUiState = DetailUiState.Error("error happened"),
                onBackClick = {},
                navigateToDetail = {}
            )
        }
        with(composeTestRule) {
            onNodeWithContentDescription("loading").assertDoesNotExist()
            onNodeWithText("error happened").assertExists()
        }
    }

    @Test
    fun should_display_detail_data_when_state_is_success() {
        val fakeData = DetailData(
            id = 6,
            name = "pikachu",
            image = "",
            types = listOf("electric", "ground"),
            evolvesFrom = EvolvesFrom(2, "charmander", ""),
            description = "description"
        )
        composeTestRule.setContent {
            DetailScreen(
                detailUiState = DetailUiState.Success(data = fakeData),
                onBackClick = {},
                navigateToDetail = { pokemonId ->
                    assertEquals(2, pokemonId)
                }
            )
        }
        with(composeTestRule) {
            onNodeWithText("#6").assertExists()
            onNodeWithText("pikachu").assertExists()
            onNodeWithText("electric").assertExists()
            onNodeWithText("ground").assertExists()
            onNodeWithText(activity.getString(R.string.evolves_from)).assertExists()
            onNodeWithText("charmander").assertExists().performClick()
            onNodeWithText("description").assertExists()
        }
    }

    @Test
    fun should_not_display_evolves_from_when_evolves_from_is_null() {
        val fakeData = DetailData(
            id = 6,
            name = "pikachu",
            image = "",
            types = listOf("electric", "ground"),
            evolvesFrom = null,
            description = "description"
        )
        composeTestRule.setContent {
            DetailScreen(
                detailUiState = DetailUiState.Success(data = fakeData),
                onBackClick = {},
                navigateToDetail = {}
            )
        }
        with(composeTestRule) {
            onNodeWithText("pikachu").assertExists()
            onNodeWithText(activity.getString(R.string.evolves_from)).assertDoesNotExist()
            onNodeWithText("description").assertExists()
        }
    }
}

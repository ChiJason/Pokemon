package com.example.pokemon

import com.example.pokemon.data.PokemonRepository
import com.example.pokemon.data.db.CapturedPokemon
import com.example.pokemon.data.db.Pokemon
import com.example.pokemon.data.db.Type
import com.example.pokemon.data.db.TypeWithPokemons
import com.example.pokemon.ui.home.HomeViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    private val repo: PokemonRepository = mockk(relaxed = true)
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() = runTest {
        every { repo.getTypesWithPokemons() } returns flowOf(
            listOf(
                TypeWithPokemons(
                    type = Type("fire"),
                    pokemons = listOf(Pokemon(0, "name0", "image0"))
                ),
                TypeWithPokemons(
                    type = Type("water"),
                    pokemons = listOf(
                        Pokemon(1, "name1", "image1"),
                        Pokemon(2, "name2", "image2")
                    )
                )
            )
        )
        every { repo.getCapturedPokemons() } returns flowOf(
            listOf(
                CapturedPokemon(0, 0, "name0", "image0"),
                CapturedPokemon(1, 0, "name0", "image0"),
            )
        )
        viewModel = HomeViewModel(repo)

        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.collections.collect()
        }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.pocketItems.collect()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should received collection and pocket items when received data`() = runTest {

        val collectionItems = viewModel.collections.value
        assertEquals(2, collectionItems.size)
        assertEquals("Fire", collectionItems[0].type)
        assertEquals("Water", collectionItems[1].type)
        assertEquals(1, collectionItems[0].pokemonItems.size)
        assertEquals(2, collectionItems[1].pokemonItems.size)

        val pocketItems = viewModel.pocketItems.value
        assertEquals(2, pocketItems.size)
        assertEquals("Name0", pocketItems[0].name)
    }

    @Test
    fun `should call capture pokemon when capture pokemon`() {

        viewModel.capturePokemon(123)
        coVerify { repo.capturePokemon(123) }
    }

    @Test
    fun `should call release pokemon when release pokemon`() {

        viewModel.releasePokemon(123)
        coVerify { repo.releasePokemon(123) }
    }

}

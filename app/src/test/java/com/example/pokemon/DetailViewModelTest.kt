package com.example.pokemon

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.example.pokemon.data.PokemonRepository
import com.example.pokemon.data.db.Pokemon
import com.example.pokemon.data.db.PokemonWithTypes
import com.example.pokemon.data.db.Species
import com.example.pokemon.data.db.SpeciesWithEvolvesFrom
import com.example.pokemon.data.db.Type
import com.example.pokemon.ui.detail.Detail
import com.example.pokemon.ui.detail.DetailUiState
import com.example.pokemon.ui.detail.DetailViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetailViewModelTest {

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    private val repo: PokemonRepository = mockk(relaxed = true)
    private val savedStateHandle: SavedStateHandle = mockk(relaxed = true)
    private lateinit var viewModel: DetailViewModel

    @Before
    fun setup() = runTest {
        every { repo.getPokemonWithTypes(any()) } returns flowOf(
            PokemonWithTypes(
                Pokemon(123, "name", "image"),
                types = listOf(Type("fire"))
            )
        )
        every { repo.getSpeciesWIthEvolvesFrom(any()) } returns flowOf(
            SpeciesWithEvolvesFrom(
                species = Species(123, evolvesFromId = 1, "description"),
                evolvesFrom = Pokemon(1, "name", "image")
            )
        )
        mockkStatic("androidx.navigation.SavedStateHandleKt") //Temporary solution
        every { savedStateHandle.toRoute<Detail>() } returns Detail(123)
        viewModel = DetailViewModel(savedStateHandle, repo)
        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.detail.collect {}
        }
    }

    @Test
    fun `should received pokemon detail when received data`() = runTest {

        val detail = (viewModel.detail.value as DetailUiState.Success).data

        assertEquals(123, detail.id)
        assertEquals("Name", detail.name)
        assertEquals(1, detail.types.size)
        assertEquals(1L, detail.evolvesFrom?.id)
        assertEquals("Name", detail.evolvesFrom?.name)
        assertEquals("description", detail.description)

        coVerify { repo.getPokemonWithTypes(123) }
        coVerify { repo.getSpeciesWIthEvolvesFrom(123) }
    }

    @Test
    fun `should received error state when error occurred`() = runTest {

        val repo = mockk<PokemonRepository>(relaxed = true)
        every { repo.getPokemonWithTypes(any()) } returns flow { throw RuntimeException("error") }
        every { repo.getSpeciesWIthEvolvesFrom(any()) } returns flow { throw RuntimeException("error") }
        val viewModel = DetailViewModel(savedStateHandle, repo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.detail.collect {}
        }

        assertTrue(viewModel.detail.value is DetailUiState.Error)
        assertEquals("error", (viewModel.detail.value as DetailUiState.Error).errorMessage)
    }

}

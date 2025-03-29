package com.example.pokemon

import com.example.pokemon.data.PokemonRepository
import com.example.pokemon.data.db.Pokemon
import com.example.pokemon.data.db.PokemonDao
import com.example.pokemon.data.db.Species
import com.example.pokemon.data.db.SpeciesWithEvolvesFrom
import com.example.pokemon.data.db.TypePokemonCrossRef
import com.example.pokemon.data.network.FlavorTextEntry
import com.example.pokemon.data.network.OfficialArtwork
import com.example.pokemon.data.network.Other
import com.example.pokemon.data.network.PokemonCollection
import com.example.pokemon.data.network.PokemonInfo
import com.example.pokemon.data.network.PokemonName
import com.example.pokemon.data.network.PokemonService
import com.example.pokemon.data.network.PokemonSpecies
import com.example.pokemon.data.network.Sprites
import com.example.pokemon.data.network.Type
import com.example.pokemon.data.network.TypeCollection
import com.example.pokemon.data.network.Version
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PokemonRepositoryTest {

    @get:Rule
    val coroutineRule = CoroutinesTestRule()

    private val pokemonService: PokemonService = mockk(relaxed = true)
    private val pokemonDao: PokemonDao = mockk(relaxed = true)

    @Test
    fun `should fetch pokemon list if not fully synced`() = runTest {
        val names = listOf(
            PokemonName(name = "pokemon1"),
            PokemonName(name = "pokemon2"),
            PokemonName(name = "pokemon3")
        )
        val infos = listOf(
            PokemonInfo(
                0,
                "pokemon1",
                Sprites(Other(OfficialArtwork("imageUrl"))),
                listOf(TypeCollection(Type("type1")))
            ),
            PokemonInfo(
                1,
                "pokemon2",
                Sprites(Other(OfficialArtwork("imageUrl"))),
                listOf(TypeCollection(Type("type2")))
            ),
            PokemonInfo(
                2,
                "pokemon3",
                Sprites(Other(OfficialArtwork("imageUrl"))),
                listOf(TypeCollection(Type("type3")))
            )
        )
        coEvery { pokemonDao.getLastPokemonId() } returns 100
        coEvery {
            pokemonService.fetchPokemonList(
                any(),
                any()
            )
        } returns PokemonCollection(results = names)
        coEvery { pokemonDao.getPokemonIdByName(any()) } returns null
        coEvery { pokemonService.fetchPokemonInfo(any()) } returnsMany infos

        val repo = PokemonRepository(pokemonService, pokemonDao)
        repo.fetchPokemonList()

        val pokemonSlots = mutableListOf<Pokemon>()
        val typeSlots = mutableListOf<List<com.example.pokemon.data.db.Type>>()
        val crossRefSlots = mutableListOf<List<TypePokemonCrossRef>>()
        coVerify { pokemonService.fetchPokemonList(offset = 100) }
        coVerify(exactly = 3) { pokemonDao.getPokemonIdByName(any()) }
        coVerify(exactly = 3) { pokemonService.fetchPokemonInfo(any()) }
        coVerify(exactly = 3) {
            pokemonDao.insertPokemonAndTypes(
                capture(pokemonSlots),
                capture(typeSlots),
                capture(crossRefSlots)
            )
        }
        val firstPokemon = pokemonSlots.first()
        val firstType = typeSlots.first().first()
        val firstCrossRef = crossRefSlots.first().first()

        assertEquals("pokemon1", firstPokemon.name)
        assertEquals("type1", firstType.typeName)
        assertEquals(firstPokemon.pokemonId, firstCrossRef.pokemonId)
        assertEquals(firstType.typeName, firstCrossRef.typeName)
    }

    @Test
    fun `should not fetch pokemon list if fully synced`() = runTest {

        coEvery { pokemonDao.getLastPokemonId() } returns 151

        val repo = PokemonRepository(pokemonService, pokemonDao)
        repo.fetchPokemonList()

        coVerify(inverse = true) { pokemonService.fetchPokemonList(any(), any()) }
        coVerify(inverse = true) { pokemonDao.getPokemonIdByName(any()) }
        coVerify(inverse = true) { pokemonService.fetchPokemonInfo(any()) }
        coVerify(inverse = true) { pokemonDao.insertPokemonAndTypes(any(), any(), any()) }
    }

    @Test
    fun `should fetch pokemon species if not cached`() = runTest {

        val fakeSpecies = PokemonSpecies(
            evolvesFromSpecies = PokemonName("pokemon1"),
            flavorTextEntries = listOf(
                FlavorTextEntry(flavorText = "blue description", version = Version("blue")),
                FlavorTextEntry(flavorText = "red description", version = Version("red")),
            )
        )
        val expect = SpeciesWithEvolvesFrom(
            Species(0, 100, "description"),
            Pokemon(100, "pokemon1", "")
        )

        coEvery { pokemonDao.getSpeciesWIthEvolvesFrom(any()) } returns flowOf(expect)
        coEvery { pokemonDao.getSpecies(any()) } returns null
        coEvery { pokemonService.fetchPokemonSpecies(any()) } returns fakeSpecies
        coEvery { pokemonDao.getPokemonIdByName(any()) } returns 100

        val repo = PokemonRepository(pokemonService, pokemonDao)

        val result = repo.getSpeciesWIthEvolvesFrom(0).first()
        assertEquals(expect, result)

        coVerify { pokemonDao.getSpecies(0) }
        coVerify { pokemonService.fetchPokemonSpecies(0) }
        coVerify { pokemonDao.getPokemonIdByName("pokemon1") }
        coVerify {
            pokemonDao.insertSpecies(
                Species(
                    pokemonId = 0,
                    evolvesFromId = 100,
                    description = "red description"
                )
            )
        }
    }

    @Test
    fun `should not fetch pokemon species if cached`() = runTest {

        val expect = SpeciesWithEvolvesFrom(
            Species(0, 100, "description"),
            Pokemon(100, "pokemon1", "")
        )
        coEvery { pokemonDao.getSpeciesWIthEvolvesFrom(any()) } returns flowOf(expect)
        coEvery { pokemonDao.getSpecies(any()) } returns Species(0, 100, "description")

        val repo = PokemonRepository(pokemonService, pokemonDao)
        val result = repo.getSpeciesWIthEvolvesFrom(0).first()
        assertEquals(expect, result)

        coVerify(inverse = true) { pokemonService.fetchPokemonSpecies(any()) }
        coVerify(inverse = true) { pokemonDao.getPokemonIdByName(any()) }
        coVerify(inverse = true) { pokemonDao.insertSpecies(any()) }
    }

    @Test
    fun `should update pocket pokemon when capture or release`() = runTest {

        val repo = PokemonRepository(pokemonService, pokemonDao)

        repo.capturePokemon(0)
        coVerify { pokemonDao.insertPocket(any()) }

        repo.releasePokemon(0)
        coVerify { pokemonDao.deletePocket(0) }
    }

}

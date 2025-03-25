package com.example.pokemon.data.network

import com.example.pokemon.data.db.Pokemon
import com.example.pokemon.data.db.PokemonDao
import com.example.pokemon.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepository @Inject constructor(
    private val service: PokemonService,
    private val pokemonDao: PokemonDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun fetchPrepopulatedData() = withContext(ioDispatcher) {
        coroutineScope {
            val pokemonNames = service.fetchPokemonList().results.map { it.name }
            val pokemonList = pokemonNames.map { async { fetchPokemon(it) } }
            val pokemon = pokemonList.awaitAll()
            pokemonDao.insertAll(*pokemon.toTypedArray())
        }
    }

    private suspend fun fetchPokemon(name: String): Pokemon =
        coroutineScope {
            val pokemonInfo = async { service.fetchPokemonInfo(name) }
            val pokemonSpecies = async { service.fetchPokemonSpecies(name) }
            val info = pokemonInfo.await()
            val species = pokemonSpecies.await()
            Pokemon(
                id = info.id,
                name = info.name,
                image = info.sprites.other.officialArtwork.frontDefault,
                description = species.flavorTextEntries.firstOrNull()?.flavorText.orEmpty(),
                types = info.types.map { it.type.name },
                evolvesFromSpecies = species.evolvesFromSpecies?.name
            )
        }

    fun getAllPokemon() = pokemonDao.getAllPokemon().distinctUntilChanged()
}

package com.example.pokemon.data

import com.example.pokemon.data.db.Pocket
import com.example.pokemon.data.db.Pokemon
import com.example.pokemon.data.db.PokemonDao
import com.example.pokemon.data.db.PokemonDetail
import com.example.pokemon.data.db.Species
import com.example.pokemon.data.db.Type
import com.example.pokemon.data.db.TypePokemonCrossRef
import com.example.pokemon.data.network.PokemonService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepository @Inject constructor(
    private val service: PokemonService,
    private val pokemonDao: PokemonDao
) {

    fun getTypesWithPokemons() = pokemonDao.getTypesWithPokemons()

    fun getCapturedPokemons() = pokemonDao.getRecentCapturedPokemons()

    //Option 1
    fun getPokemonWithTypes(pokemonId: Long) = pokemonDao.getPokemonWithTypes(pokemonId)

    //Option 1
    fun getSpeciesWIthEvolvesFrom(pokemonId: Long) = pokemonDao.getSpeciesWIthEvolvesFrom(pokemonId)
        .onStart { fetchPokemonSpecies(pokemonId) }

    //Option 2
    fun getPokemonDetail(pokemonId: Long): Flow<PokemonDetail> =
        pokemonDao.getPokemonDetail(pokemonId).retry(1) {
            fetchPokemonSpecies(pokemonId)
            true
        }

    suspend fun capturePokemon(pokemonId: Long) =
        pokemonDao.insertPocket(pocket = Pocket(pokemonId = pokemonId, capturedAt = Date()))

    suspend fun releasePokemon(pocketId: Long) = pokemonDao.deletePocket(pocketId)

    suspend fun fetchPokemonList() {
        coroutineScope {
            val lastPokemonId = pokemonDao.getLastPokemonId()
            if (lastPokemonId == 151) return@coroutineScope
            val pokemonNames =
                service.fetchPokemonList(offset = lastPokemonId).results.map { it.name }
            pokemonNames.map { async { fetchPokemon(it) } }.awaitAll()
        }
    }

    private suspend fun fetchPokemon(name: String) {
        if (pokemonDao.getPokemonIdByName(name) != null) return
        val pokemonInfo = service.fetchPokemonInfo(name)
        val pokemon = Pokemon(
            pokemonId = pokemonInfo.id,
            name = pokemonInfo.name,
            image = pokemonInfo.sprites.other.officialArtwork.frontDefault
        )
        val (types, crossRefs) = pokemonInfo.types.map { it.type.name }.map {
            Pair(
                Type(typeName = it),
                TypePokemonCrossRef(typeName = it, pokemonId = pokemon.pokemonId)
            )
        }.unzip()
        pokemonDao.insertPokemonAndTypes(pokemon, types, crossRefs)
    }

    private suspend fun fetchPokemonSpecies(pokemonId: Long) {
        if (pokemonDao.getSpecies(pokemonId) != null) return
        val species = service.fetchPokemonSpecies(pokemonId)
        val evolvesFromId = species.evolvesFromSpecies?.name?.let {
            pokemonDao.getPokemonIdByName(it)
        }
        pokemonDao.insertSpecies(
            Species(
                pokemonId = pokemonId,
                evolvesFromId = evolvesFromId,
                description = species.flavorTextEntries.firstOrNull {
                    it.version.name == "red"
                }?.flavorText.orEmpty()
            )
        )
    }
}

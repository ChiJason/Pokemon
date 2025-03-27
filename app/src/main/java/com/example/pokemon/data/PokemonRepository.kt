package com.example.pokemon.data

import android.util.Log
import com.example.pokemon.data.db.Pokemon
import com.example.pokemon.data.db.PokemonDao
import com.example.pokemon.data.network.PokemonService
import com.example.pokemon.di.AppDispatchers.IO
import com.example.pokemon.di.Dispatcher
import com.example.pokemon.ui.detail.DetailUiState
import com.example.pokemon.ui.detail.EvolvesFrom
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepository @Inject constructor(
    private val service: PokemonService,
    private val pokemonDao: PokemonDao,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
) {

    val fakeStorage = MutableStateFlow<Map<String, Set<Pokemon>>>(mapOf())

    suspend fun fetchPokemonList() = withContext(ioDispatcher) {
        coroutineScope {
            Log.d("PokemonRepository", "fetchPokemonList: start")
            val pokemonNames = service.fetchPokemonList().results.map { it.name }
            pokemonNames.map { async { fetchPokemon(it) } }.awaitAll()
            Log.d(
                "PokemonRepository",
                "fetchPokemonList: end ${fakeStorage.value.values.flatten().toSet().size}"
            )
        }
    }

    private suspend fun fetchPokemon(name: String) = service.fetchPokemonInfo(name).let {
        val pokemon = Pokemon(
            it.id,
            it.name.replaceFirstChar(Char::uppercase),
            it.sprites.other.officialArtwork.frontDefault
        )
        it.types.map { it.type.name }.forEach {
            fakeStorage.update { storage ->
                val new = storage.toMutableMap()
                new[it] = new[it]?.plus(pokemon) ?: setOf(pokemon)
                new.toSortedMap()
            }
        }
    }

    fun getPokemonDetail(id: Long) = flow {
        val pokemons = fakeStorage.value.values.flatten().toSet()
        val species = service.fetchPokemonSpecies(id)
        val pokemon = pokemons.find { it.id == id }
        val detail = DetailUiState(
            id = id,
            name = pokemon?.name.orEmpty(),
            image = pokemon?.image.orEmpty(),
            types = fakeStorage.value.filterValues { it.any { it.id == id } }.keys.toList(),
            evolvesFrom = species.evolvesFromSpecies?.let { from ->
                pokemons.find { it.name.equals(from.name, true) }?.let {
                    EvolvesFrom(
                        it.id,
                        it.name,
                        it.image
                    )
                }
            },
            description = species.flavorTextEntries.firstOrNull {
                it.version.name == "red"
            }?.flavorText.orEmpty()
        )
        emit(detail)
    }.flowOn(ioDispatcher)

    fun getAllPokemon() = pokemonDao.getAllPokemon().distinctUntilChanged()
}

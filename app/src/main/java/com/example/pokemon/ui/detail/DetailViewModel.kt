package com.example.pokemon.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.pokemon.data.PokemonRepository
import com.example.pokemon.data.db.PokemonDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repo: PokemonRepository
) : ViewModel() {

    private val pokemonId: Long = savedStateHandle.toRoute<Detail>().pokemonId

    //Option 1
    val detail = toDetailUiState(
        pokemonId = pokemonId,
        repo = repo
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailUiState.Loading
    )

//    Option 2
//    val detail = repo.getPokemonDetail(pokemonId)
//        .map { it.toUiState() }
//        .catch { error ->
//            errorMessage = Event(error.message.orEmpty())
//        }.stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = DetailUiState()
//        )
}

private fun toDetailUiState(pokemonId: Long, repo: PokemonRepository) = combine(
    repo.getPokemonWithTypes(pokemonId),
    repo.getSpeciesWIthEvolvesFrom(pokemonId),
) { pokemonWithTypes, speciesWithEvolvesFrom ->
    val result = DetailUiState.Success(
        data = DetailData(
            id = pokemonWithTypes.pokemon.pokemonId,
            name = pokemonWithTypes.pokemon.name.replaceFirstChar(Char::uppercase),
            image = pokemonWithTypes.pokemon.image,
            types = pokemonWithTypes.types.map { it.typeName },
            evolvesFrom = speciesWithEvolvesFrom.evolvesFrom?.let {
                EvolvesFrom(
                    id = it.pokemonId,
                    name = it.name.replaceFirstChar(Char::uppercase),
                    image = it.image
                )
            },
            description = speciesWithEvolvesFrom.species.description
        )
    )
    Result.success(result)
}.catch {
    emit(Result.failure(it))
}.map {
    it.getOrElse {
        val error = if (it is UnknownHostException) "Connection Failed" else it.message.orEmpty()
        DetailUiState.Error(error)
    }
}

private fun PokemonDetail.toUiState() = DetailData(
    id = pokemon.pokemonId,
    name = pokemon.name,
    image = pokemon.image,
    types = pokemonWithTypes.types.map { it.typeName },
    evolvesFrom = speciesWithEvolvesFrom.evolvesFrom?.let {
        EvolvesFrom(
            id = it.pokemonId,
            name = it.name,
            image = it.image
        )
    },
    description = speciesWithEvolvesFrom.species.description
)

sealed class DetailUiState {
    data object Loading : DetailUiState()
    data class Success(val data: DetailData) : DetailUiState()
    data class Error(val errorMessage: String) : DetailUiState()
}

data class DetailData(
    val id: Long = 0,
    val name: String = "",
    val image: String = "",
    val types: List<String> = emptyList(),
    val evolvesFrom: EvolvesFrom? = null,
    val description: String = ""
)

data class EvolvesFrom(
    val id: Long,
    val name: String,
    val image: String
)

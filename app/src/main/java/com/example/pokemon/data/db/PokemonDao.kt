package com.example.pokemon.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(vararg pokemon: Pokemon)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertType(vararg type: Type): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTypePokemonCrossRef(vararg crossRef: TypePokemonCrossRef)

    @Transaction
    suspend fun insertPokemonAndTypes(
        pokemon: Pokemon,
        types: List<Type>,
        crossRefs: List<TypePokemonCrossRef>
    ) {
        insertPokemon(pokemon)
        insertType(*types.toTypedArray())
        insertTypePokemonCrossRef(*crossRefs.toTypedArray())
    }

    @Transaction
    @Query("SELECT * FROM type ORDER BY typeName ASC")
    fun getTypesWithPokemons(): Flow<List<TypeWithPokemons>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPocket(pocket: Pocket)

    @Query("DELETE FROM pocket WHERE id = :id")
    suspend fun deletePocket(vararg id: Long)

    @Query(
        """
        SELECT pocket.id, pokemon.pokemonId, pokemon.name, pokemon.image FROM pocket, pokemon
        WHERE pocket.pokemonId = pokemon.pokemonId 
        ORDER BY capturedAt DESC
    """
    )
    fun getRecentCapturedPokemons(): Flow<List<CapturedPokemon>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecies(vararg species: Species)

    @Query("SELECT * FROM species WHERE pokemonId = :pokemonId")
    suspend fun getSpecies(pokemonId: Long): Species?

    @Query("SELECT * FROM species WHERE pokemonId = :pokemonId")
    fun getSpeciesWIthEvolvesFrom(pokemonId: Long): Flow<SpeciesWithEvolvesFrom>

    @Transaction
    @Query("SELECT * FROM pokemon WHERE pokemonId = :pokemonId")
    fun getPokemonWithTypes(pokemonId: Long): Flow<PokemonWithTypes>

    @Query("SELECT * FROM pokemon WHERE pokemonId = :pokemonId")
    fun getPokemonDetail(pokemonId: Long): Flow<PokemonDetail>

    @Query("SELECT pokemonId FROM pokemon WHERE name = :name")
    suspend fun getPokemonIdByName(name: String): Long?

    @Query("SELECT pokemonId FROM pokemon ORDER BY pokemonId DESC LIMIT 1")
    suspend fun getLastPokemonId(): Int
}

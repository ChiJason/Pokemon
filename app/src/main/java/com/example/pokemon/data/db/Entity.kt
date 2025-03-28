package com.example.pokemon.data.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Date

@Entity
data class Pokemon(
    @PrimaryKey val pokemonId: Long,
    val name: String,
    val image: String
)

@Entity
data class Type(@PrimaryKey val typeName: String)

@Entity(primaryKeys = ["typeName", "pokemonId"])
data class TypePokemonCrossRef(
    val typeName: String,
    val pokemonId: Long
)

@Entity
data class Species(
    @PrimaryKey val pokemonId: Long,
    @ColumnInfo(name = "evolves_from_id") val evolvesFromId: Long?,
    val description: String
)

@Entity
data class Pocket(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pokemonId: Long,
    val capturedAt: Date
)

data class TypeWithPokemons(
    @Embedded val type: Type,
    @Relation(
        parentColumn = "typeName",
        entityColumn = "pokemonId",
        associateBy = Junction(TypePokemonCrossRef::class)
    )
    val pokemons: List<Pokemon>
)

data class PokemonWithTypes(
    @Embedded val pokemon: Pokemon,
    @Relation(
        parentColumn = "pokemonId",
        entityColumn = "typeName",
        associateBy = Junction(TypePokemonCrossRef::class)
    )
    val types: List<Type>
)

data class CapturedPokemon(
    val id: Long,
    val pokemonId: Long,
    val name: String,
    val image: String
)

data class SpeciesWithEvolvesFrom(
    @Embedded val species: Species,
    @Relation(
        parentColumn = "evolves_from_id",
        entityColumn = "pokemonId"
    )
    val evolvesFrom: Pokemon?
)

data class PokemonDetail(
    @Embedded val pokemon: Pokemon,
    @Relation(
        entity = Pokemon::class,
        parentColumn = "pokemonId",
        entityColumn = "pokemonId"
    )
    val pokemonWithTypes: PokemonWithTypes,
    @Relation(
        entity = Species::class,
        parentColumn = "pokemonId",
        entityColumn = "pokemonId"
    )
    val speciesWithEvolvesFrom: SpeciesWithEvolvesFrom,
)

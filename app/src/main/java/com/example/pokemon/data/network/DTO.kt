package com.example.pokemon.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonCollection(@SerialName("results") val results: List<PokemonName>)

@Serializable
data class PokemonName(@SerialName("name") val name: String)

@Serializable
data class PokemonInfo(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("sprites") val sprites: Sprites,
    @SerialName("types") val types: List<TypeCollection>
)

@Serializable
data class Sprites(@SerialName("other") val other: Other)

@Serializable
data class Other(@SerialName("official-artwork") val officialArtwork: OfficialArtwork)

@Serializable
data class OfficialArtwork(@SerialName("front_default") val frontDefault: String)

@Serializable
data class TypeCollection(@SerialName("type") val type: Type)

@Serializable
data class Type(@SerialName("name") val name: String)

@Serializable
data class PokemonSpecies(
    @SerialName("evolves_from_species") val evolvesFromSpecies: PokemonName?,
    @SerialName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry>
)

@Serializable
data class FlavorTextEntry(
    @SerialName("flavor_text") val flavorText: String,
    @SerialName("version") val version: Version,
)

@Serializable
data class Version(@SerialName("name") val name: String)


package com.example.pokemon.data.network

import retrofit2.http.GET
import retrofit2.http.Path


interface PokemonService {

    @GET("pokemon?limit=151")
    suspend fun fetchPokemonList(): PokemonCollection

    @GET("pokemon/{name}")
    suspend fun fetchPokemonInfo(@Path("name") name: String): PokemonInfo

    @GET("pokemon-species/{name}")
    suspend fun fetchPokemonSpecies(@Path("name") name: String): PokemonSpecies
}

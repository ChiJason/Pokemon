package com.example.pokemon.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface PokemonService {

    @GET("pokemon/")
    suspend fun fetchPokemonList(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = 151 - offset
    ): PokemonCollection

    @GET("pokemon/{name}")
    suspend fun fetchPokemonInfo(@Path("name") name: String): PokemonInfo

    @GET("pokemon-species/{id}")
    suspend fun fetchPokemonSpecies(@Path("id") id: Long): PokemonSpecies
}

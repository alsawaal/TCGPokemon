package com.ghost.tcgpokemon.model.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("cards")
    suspend fun getPokemon(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("pageSize") size: Int
    ): PokemonResponse
}
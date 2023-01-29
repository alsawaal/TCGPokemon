package com.ghost.tcgpokemon.model.network

data class PokemonResponse(
    val count: Int,
    val data: List<PokemonData>,
    val page: Int,
    val pageSize: Int,
    val totalCount: Int
)
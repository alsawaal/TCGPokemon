package com.ghost.tcgpokemon.model.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.ghost.tcgpokemon.model.data.PokemonRemoteMediator
import com.ghost.tcgpokemon.model.local.PokemonDatabase
import com.ghost.tcgpokemon.model.network.ApiService
import com.ghost.tcgpokemon.model.network.PokemonData

class PokemonRepository(
    private val pokemonDatabase: PokemonDatabase,
    private val apiService: ApiService
) {
    fun getPokemon(name: String): LiveData<PagingData<PokemonData>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                prefetchDistance = 5
            ),
            remoteMediator = PokemonRemoteMediator(
                name,
                pokemonDatabase,
                apiService
            ),
            pagingSourceFactory = {
                pokemonDatabase.pokemonDao().getAllPokemon()
            }
        ).liveData
    }

    suspend fun deleteAllPokemon() {
        pokemonDatabase.pokemonDao().deleteAll()
        pokemonDatabase.remoteKeysDao().deleteRemoteKeys()
    }

    suspend fun isPokemonEmpty(): LiveData<Boolean> = pokemonDatabase.pokemonDao().isPokemonEmpty()
}
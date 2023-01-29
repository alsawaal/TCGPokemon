package com.ghost.tcgpokemon.model.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ghost.tcgpokemon.model.network.PokemonData

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(quote: List<PokemonData>)

    @Query("SELECT * FROM pokemon")
    fun getAllPokemon(): PagingSource<Int, PokemonData>

    @Query("SELECT (SELECT COUNT(*) FROM pokemon) == 0")
    fun isPokemonEmpty(): LiveData<Boolean>

    @Query("DELETE FROM pokemon")
    suspend fun deleteAll()
}
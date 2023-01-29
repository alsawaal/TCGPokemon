package com.ghost.tcgpokemon.model.network

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@Entity(tableName = "pokemon")
data class PokemonData(
    @PrimaryKey
    val id: String,
    val hp: String?,
    val level: String?,
    val name: String?,
    val types: List<String>?,
    val images: @RawValue PokemonImage?
) : Parcelable
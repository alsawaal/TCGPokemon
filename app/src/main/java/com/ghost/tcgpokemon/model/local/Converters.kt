package com.ghost.tcgpokemon.model.local

import androidx.room.TypeConverter
import com.google.gson.Gson

import com.google.gson.reflect.TypeToken
import com.ghost.tcgpokemon.model.network.PokemonImage
import java.lang.reflect.Type


class Converters {
    @TypeConverter
    fun fromString(value: String?): List<String?>? {
        val listType: Type = object : TypeToken<List<String?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String?>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromStringToImages(value: String?): PokemonImage? {
        val listType: Type = object : TypeToken<PokemonImage?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromImages(images: PokemonImage?): String? {
        val gson = Gson()
        return gson.toJson(images)
    }
}
package com.ghost.tcgpokemon.di

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.ghost.tcgpokemon.model.data.PokemonRepository
import com.ghost.tcgpokemon.model.local.PokemonDatabase
import com.ghost.tcgpokemon.model.network.ApiService
import com.ghost.tcgpokemon.ui.list.PokemonListViewModel
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    single {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.pokemontcg.io/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()

        retrofit.create(ApiService::class.java)
    }
}

val repositoryModule = module {
    factory { PokemonDatabase.getDatabase(androidContext()) }
    factory { PokemonRepository(get(), get()) }
}

val viewModelModule = module {
    viewModel { PokemonListViewModel(get()) }
}
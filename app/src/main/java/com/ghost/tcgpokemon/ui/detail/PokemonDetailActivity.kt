package com.ghost.tcgpokemon.ui.detail

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.ghost.tcgpokemon.databinding.ActivityPokemonDetailBinding
import com.ghost.tcgpokemon.model.network.PokemonData
import com.ghost.tcgpokemon.util.DialogUtil
import com.ghost.tcgpokemon.util.ImageUtil
import com.ghost.tcgpokemon.ui.list.PokemonListActivity.Companion.POKEMON_EXTRA

class PokemonDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPokemonDetailBinding
    private var pokemonName = ""
    private var pokemonImage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPokemonDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get pokemon data
        val pokemonData = getPokemonData()

        setSelectedPokemon(pokemonData)
        onBackButtonPressed()
        setPokemonBioView(pokemonData)
        setPokemonImageView()
        setOnDownloadListener()
    }

    private fun getPokemonData() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        intent.getParcelableExtra(POKEMON_EXTRA, PokemonData::class.java)

    } else {
        @Suppress("DEPRECATION")
        intent.getParcelableExtra(POKEMON_EXTRA)
    }

    private fun setSelectedPokemon(pokemonData: PokemonData?) {
        pokemonData?.let { pokemon ->
            pokemon.name?.let { name ->
                pokemonName = name
            }

            pokemon.images?.let { image ->
                pokemonImage = image.large
            }
        }
    }

    private fun onBackButtonPressed() {
        binding.ivBack.setOnClickListener { finish() }
    }

    private fun setPokemonBioView(pokemonData: PokemonData?) {
        pokemonData?.let {
            with(binding) {
                tvPokemonName.text = it.name
                tvPokemonType.text = it.types?.joinToString(", ")

                it.level?.let {
                    tvPokemonLevel.text = it
                } ?: run {
                    tvLevelLabel.visibility = View.GONE
                    tvPokemonLevel.visibility = View.GONE
                }

                tvPokemonHealth.text = it.hp
            }
        }
    }

    private fun setPokemonImageView() {
        try {
            Glide.with(applicationContext)
                .load(pokemonImage)
                .into(binding.ivPokemonImage)
        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvErrorState.visibility = View.VISIBLE
        }
    }

    private fun setOnDownloadListener() {
        binding.btnDownload.setOnClickListener {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    this,
                    WRITE_EXTERNAL_STORAGE
                ) -> downloadImage()

                else -> requestPermissionLauncher.launch(
                    WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }

    private fun downloadImage() {
        val dialog = DialogUtil(this)
        ImageUtil.saveToFile(pokemonImage, pokemonName, this, onLoadingListener = {
            runOnUiThread {
                Log.d("TAG", "setOnDownloadListener: Loading ...")
                dialog.showDownloadDialog(this, "Please Wait")
            }
        }) { message ->
            runOnUiThread {
                Log.d("TAG", "setOnDownloadListener: Success")
                dialog.dismissDownloadDialog()
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            downloadImage()
        } else {
            Toast.makeText(
                applicationContext,
                "You have to grant the permission to save the card's picture.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
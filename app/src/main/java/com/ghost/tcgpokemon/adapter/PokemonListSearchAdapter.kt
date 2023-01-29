package com.ghost.tcgpokemon.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ghost.tcgpokemon.databinding.ItemPokemonSearchBinding
import com.ghost.tcgpokemon.model.network.PokemonData

class PokemonListSearchAdapter: PagingDataAdapter<PokemonData, PokemonListSearchAdapter.MyViewHolder> (
    DIFF_CALLBACK
        ){
    var onItemClick: ((PokemonData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemPokemonSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    inner class MyViewHolder(private val binding: ItemPokemonSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: PokemonData) {
            Log.d("TAG", "bind data: $data")
            binding.tvPokemonName.text = data.name
            Glide.with(binding.root)
                .load(data.images?.small)
                .into(binding.ivPokemonImage)

            binding.cardItemPokemonSearch.setOnClickListener {
                onItemClick?.invoke(data)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PokemonData>() {
            override fun areItemsTheSame(
                oldItem: PokemonData,
                newItem: PokemonData
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PokemonData,
                newItem: PokemonData
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
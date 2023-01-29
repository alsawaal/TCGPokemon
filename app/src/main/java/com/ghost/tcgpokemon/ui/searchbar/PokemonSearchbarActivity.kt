package com.ghost.tcgpokemon.ui.searchbar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.ghost.tcgpokemon.adapter.LoadingStateAdapter
import com.ghost.tcgpokemon.adapter.PokemonListAdapter
import com.ghost.tcgpokemon.adapter.PokemonListSearchAdapter
import com.ghost.tcgpokemon.databinding.ActivityPokemonSearchbarBinding
import com.ghost.tcgpokemon.ui.detail.PokemonDetailActivity
import com.ghost.tcgpokemon.ui.list.PokemonListActivity
import com.ghost.tcgpokemon.ui.list.PokemonListViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PokemonSearchbarActivity : AppCompatActivity() {

    private val viewModel: PokemonListViewModel by viewModel()
    private lateinit var binding: ActivityPokemonSearchbarBinding
    private lateinit var adapter: PokemonListSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPokemonSearchbarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvErrorState.visibility = View.GONE

//        initAdapter()
        onSearchPokemon()
//        onRefreshListener()
//        getData()
    }
    private fun initAdapter() {
        adapter = PokemonListSearchAdapter()

        binding.rvPokemon.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        adapter.onItemClick = { pokemonData ->
            Intent(this, PokemonDetailActivity::class.java).apply {
                putExtra(POKEMON_EXTRA, pokemonData)
                startActivity(this)
            }
        }

        adapter.addLoadStateListener { loadState ->
            // set swipe state
            onFetchLoading(loadState)

            // set error state
            lifecycleScope.launch {
                viewModel.isDatabaseEmpty().observe(
                    this@PokemonSearchbarActivity
                ) { isEmpty ->
                    Log.d(TAG, "initAdapter: $isEmpty")
                    if (isEmpty) {
                        onFetchError(loadState)
                    }
                }
            }

            // set empty state
            onFetchEmpty(loadState)
        }
    }

    private fun onFetchLoading(loadState: CombinedLoadStates) {
        if (loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading) {
            binding.swipeRefresh2.isRefreshing = true
            binding.tvErrorState.visibility = View.GONE
        } else {
            binding.rvPokemon.visibility = View.VISIBLE
            binding.swipeRefresh2.isRefreshing = false
        }
    }

    private fun onFetchError(loadState: CombinedLoadStates) {
        Log.d(TAG, "onFetchError: Should be error")
        binding.tvErrorState.visibility = View.VISIBLE
        binding.rvPokemon.visibility = View.GONE
        onFetchEmpty(loadState)
    }

    private fun onFetchEmpty(loadState: CombinedLoadStates) {
        if (loadState.append.endOfPaginationReached) {
            if (adapter.itemCount < 1) {
                Log.d(TAG, "onFetchEmpty: Should be empty")
                binding.tvErrorState.visibility = View.VISIBLE
            }
        }
    }

    private fun onSearchPokemon() {
        binding.btnSearch.setOnClickListener {
            // delete all pokemon first from database
            viewModel.deleteAllPokemon()

            // get data by query
            val query = binding.etSearch.text.toString()
            initAdapter()
            onRefreshListener()
            getData(query)
        }
    }

    private fun onRefreshListener() {
        binding.swipeRefresh2.setOnRefreshListener {
            // delete all pokemon first from database
            viewModel.deleteAllPokemon()

            // delete search input text
            binding.etSearch.setText("")

            // get data while refreshing
            getData()
        }
    }

    private fun getData(query: String = "") {
        viewModel.getPokemon(query).observe(this) {
            it?.let {
                adapter.submitData(lifecycle, it)
                Log.d(TAG, "getData: ${adapter.itemCount}")
            }
        }
    }

    companion object {
        private val TAG = PokemonListActivity::class.java.simpleName
        const val POKEMON_EXTRA = "Pokemon Data"
    }
}
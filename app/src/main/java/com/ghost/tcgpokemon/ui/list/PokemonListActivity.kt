package com.ghost.tcgpokemon.ui.list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.ghost.tcgpokemon.adapter.LoadingStateAdapter
import com.ghost.tcgpokemon.adapter.PokemonListAdapter
import com.ghost.tcgpokemon.databinding.ActivityPokemonListBinding
import com.ghost.tcgpokemon.ui.detail.PokemonDetailActivity
import com.ghost.tcgpokemon.ui.searchbar.PokemonSearchbarActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PokemonListActivity : AppCompatActivity() {

    private val viewModel: PokemonListViewModel by viewModel()
    private lateinit var binding: ActivityPokemonListBinding
    private lateinit var adapter: PokemonListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPokemonListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        onSearchPokemon()
        onRefreshListener()
        getData()
    }

    private fun initAdapter() {
        adapter = PokemonListAdapter()

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
                    this@PokemonListActivity
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
            binding.swipeRefresh.isRefreshing = true
            binding.tvErrorState.visibility = View.GONE
        } else {
            binding.rvPokemon.visibility = View.VISIBLE
            binding.swipeRefresh.isRefreshing = false
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
        binding.etSearch.setOnClickListener{
//            startActivity(Intent(this, RegisterActivity::class.java))
            startActivity(Intent(this@PokemonListActivity, PokemonSearchbarActivity::class.java))
        }
        binding.btnSearch.setOnClickListener {
            startActivity(Intent(this@PokemonListActivity, PokemonSearchbarActivity::class.java))

//            // delete all pokemon first from database
//            viewModel.deleteAllPokemon()
//
//            // get data by query
//            val query = binding.etSearch.text.toString()
//            getData(query)
        }
    }

    private fun onRefreshListener() {
        binding.swipeRefresh.setOnRefreshListener {
            // delete all pokemon first from database
            viewModel.deleteAllPokemon()

            // delete search input text
//            binding.etSearch.setText("")

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
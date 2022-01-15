package com.example.moviedb.features.watchlist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviedb.MainActivity
import com.example.moviedb.R
import com.example.moviedb.databinding.FragmentHomeBinding
import com.example.moviedb.databinding.FragmentWatchlistBinding
import com.example.moviedb.shared.ListItemAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class WatchlistFragment : Fragment(R.layout.fragment_watchlist),
    MainActivity.OnBottomNavigationFragmentReselectedListener {

    private val viewModel: WatchlistViewModel by viewModels()

    private var currentBinding: FragmentWatchlistBinding? = null
    private val binding get() = currentBinding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentBinding = FragmentWatchlistBinding.bind(view)

        val watchlistAdapter = ListItemAdapter(
            onItemClick = { media ->

            },
            onWatchlistClick = { media ->
                viewModel.onWatchlistClick(media)
            }
        )

        binding.apply {
            recyclerViewWatchlist.apply {
                adapter = watchlistAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.watchlist.collect {
                    val media = it ?: return@collect

                    watchlistAdapter.submitList(media)
                    textViewNoWatchlist.isVisible = media.isEmpty()
                    recyclerViewWatchlist.isVisible = media.isNotEmpty()
                }
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_watchlist, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_delete_watchlist -> {
                viewModel.onDeleteWatchlist()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onBottomNavigationFragmentReselected() {
        binding.recyclerViewWatchlist.scrollToPosition(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentBinding = null
    }
}
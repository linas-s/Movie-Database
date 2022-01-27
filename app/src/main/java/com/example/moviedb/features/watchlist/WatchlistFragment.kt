package com.example.moviedb.features.watchlist

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviedb.R
import com.example.moviedb.databinding.FragmentWatchlistBinding
import com.example.moviedb.shared.ListItemAdapter
import com.example.moviedb.util.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class WatchlistFragment : Fragment(R.layout.fragment_watchlist) {

    private val viewModel: WatchlistViewModel by viewModels()

    private var currentBinding: FragmentWatchlistBinding? = null
    private val binding get() = currentBinding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentBinding = FragmentWatchlistBinding.bind(view)

        val watchlistAdapter = ListItemAdapter(
            onItemClick = { item ->
                viewModel.onWatchlistItemClick(item)
            },
            onWatchlistClick = { item ->
                viewModel.onWatchlistClick(item)
            }
        )

        watchlistAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        binding.apply {

            toolbar.apply {
                title = "Watchlist"
                inflateMenu(R.menu.menu_watchlist)
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_delete_watchlist -> {
                            viewModel.onDeleteWatchlist()
                            true
                        }
                        else -> super.onOptionsItemSelected(item)
                    }
                }
            }

            recyclerViewWatchlist.apply {
                adapter = watchlistAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.watchlist.collect {
                    val result = it ?: return@collect

                    watchlistAdapter.submitList(result)
                    textViewNoWatchlist.isVisible = result.isEmpty()
                    recyclerViewWatchlist.isVisible = result.isNotEmpty()
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.events.collect { event ->
                    when (event) {
                        is WatchlistViewModel.Event.NavigateToMediaDetailsFragment -> {
                            val action =
                                WatchlistFragmentDirections.actionWatchlistFragmentToMediaDetailsFragment(
                                    event.listItem
                                )
                            findNavController().navigate(action)
                        }
                    }.exhaustive
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentBinding = null
    }
}
package com.example.moviedb.features.search

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviedb.MainActivity
import com.example.moviedb.R
import com.example.moviedb.databinding.FragmentSearchBinding
import com.example.moviedb.features.home.HomeFragmentDirections
import com.example.moviedb.features.home.HomeViewModel
import com.example.moviedb.util.exhaustive
import com.example.moviedb.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search){

    private val viewModel: SearchViewModel by viewModels()

    private var currentBinding: FragmentSearchBinding? = null
    private val binding get() = currentBinding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentBinding = FragmentSearchBinding.bind(view)

        val searchListItemAdapter = SearchListItemPagingAdapter(
            onItemClick = { media ->
                viewModel.onSearchItemClick(media)
            },
            onWatchlistClick = { media ->
                viewModel.onWatchlistClick(media)
            }
        )

        binding.apply {

            toolbar.apply {
                setNavigationOnClickListener { findNavController().navigateUp() }
                title = "Search"
                inflateMenu(R.menu.menu_search_media)
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem?.actionView as SearchView

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (!query.isNullOrBlank()) {
                            viewModel.onSearchQuerySubmit(query)
                            searchView.clearFocus()
                        }
                        return true
                    }

                    override fun onQueryTextChange(query: String?): Boolean {
                        if (!query.isNullOrBlank()) {
                            viewModel.onSearchQuerySubmit(query)
                        }
                        return true
                    }
                })
            }

            requireActivity().window.statusBarColor = Color.parseColor("#445565")

            recyclerViewSearch.apply {
                adapter = searchListItemAdapter.withLoadStateFooter(
                    SearchListItemLoadStateAdapter(searchListItemAdapter::retry)
                )
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                itemAnimator?.changeDuration = 0
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.searchResults.collectLatest { data ->
                    searchListItemAdapter.submitData(data)
                }
            }

            swipeRefreshLayout.isEnabled = false

            textViewInstructions.isVisible = true

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                searchListItemAdapter.loadStateFlow
                    .collect { loadState ->
                        when (val refresh = loadState.mediator?.refresh) {
                            is LoadState.Loading -> {
                                textViewError.isVisible = false
                                buttonRetry.isVisible = false
                                swipeRefreshLayout.isRefreshing = true
                                textViewNoResults.isVisible = false
                                recyclerViewSearch.isVisible = searchListItemAdapter.itemCount > 0
                                textViewInstructions.isVisible = false

                                viewModel.refreshInProgress = true
                            }
                            is LoadState.NotLoading -> {
                                textViewError.isVisible = false
                                buttonRetry.isVisible = false
                                swipeRefreshLayout.isRefreshing = false
                                recyclerViewSearch.isVisible = searchListItemAdapter.itemCount > 0
                                textViewInstructions.isVisible = false

                                val noResults =
                                    searchListItemAdapter.itemCount < 1 && loadState.append.endOfPaginationReached
                                            && loadState.source.append.endOfPaginationReached

                                textViewNoResults.isVisible = noResults

                                viewModel.refreshInProgress = false
                            }
                            is LoadState.Error -> {
                                swipeRefreshLayout.isRefreshing = false
                                textViewNoResults.isVisible = false
                                textViewInstructions.isVisible = false
                                recyclerViewSearch.isVisible = searchListItemAdapter.itemCount > 0

                                val noCachedResults =
                                    searchListItemAdapter.itemCount < 1 && loadState.source.append.endOfPaginationReached

                                textViewError.isVisible = noCachedResults
                                buttonRetry.isVisible = noCachedResults

                                val errorMessage = getString(
                                    R.string.could_not_load_search_results,
                                    refresh.error.localizedMessage
                                        ?: getString(R.string.unknown_error_occurred)
                                )
                                textViewError.text = errorMessage

                                if (viewModel.refreshInProgress) {
                                    showSnackbar(errorMessage)
                                }
                                viewModel.refreshInProgress = false
                            }
                        }
                    }
            }

            swipeRefreshLayout.setOnRefreshListener {
                searchListItemAdapter.refresh()
            }

            buttonRetry.setOnClickListener {
                searchListItemAdapter.refresh()
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.events.collect { event ->
                    when (event) {
                        is SearchViewModel.Event.NavigateToMovieDetailsFragment -> {
                            val action =
                                SearchFragmentDirections.actionSearchFragmentToMovieDetailsFragment(
                                    event.movie,
                                    event.movie.title
                                )
                            findNavController().navigate(action)
                        }
                        is SearchViewModel.Event.NavigateToTvShowDetailsFragment -> TODO()
                    }.exhaustive
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewSearch.adapter = null
        currentBinding = null
    }
}
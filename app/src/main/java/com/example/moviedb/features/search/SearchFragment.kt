package com.example.moviedb.features.search

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviedb.R
import com.example.moviedb.databinding.FragmentSearchBinding
import com.example.moviedb.util.exhaustive
import com.example.moviedb.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search){

    private val viewModel: SearchViewModel by viewModels()

    private var currentBinding: FragmentSearchBinding? = null
    private val binding get() = currentBinding!!

    private lateinit var searchListItemAdapter: SearchListItemPagingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentBinding = FragmentSearchBinding.bind(view)

        requireActivity().window.statusBarColor = Color.parseColor("#445565")
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)

        searchListItemAdapter = SearchListItemPagingAdapter(
            onItemClick = { media ->
                viewModel.onSearchItemClick(media)
            },
            onWatchlistClick = { media ->
                viewModel.onWatchlistClick(media)
            }
        )

        binding.apply {

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

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.hasCurrentQuery.collect { hasCurrentQuery ->
                    textViewInstructions.isVisible = !hasCurrentQuery
                    swipeRefreshLayout.isEnabled = hasCurrentQuery

                    if (!hasCurrentQuery) {
                        recyclerViewSearch.isVisible = false
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                searchListItemAdapter.loadStateFlow
                    .distinctUntilChangedBy { it.source.refresh }
                    .filter { it.source.refresh is LoadState.NotLoading }
                    .collect {
                        if (viewModel.pendingScrollToTopAfterNewQuery) {
                            recyclerViewSearch.scrollToPosition(0)
                            viewModel.pendingScrollToTopAfterNewQuery = false
                        }
                        if (viewModel.pendingScrollToTopAfterRefresh && it.mediator?.refresh is LoadState.NotLoading) {
                            recyclerViewSearch.scrollToPosition(0)
                            viewModel.pendingScrollToTopAfterRefresh = false
                        }
                    }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                searchListItemAdapter.loadStateFlow
                    .collect { loadState ->
                        when (val refresh = loadState.mediator?.refresh) {
                            is LoadState.Loading -> {
                                textViewError.isVisible = false
                                buttonRetry.isVisible = false
                                swipeRefreshLayout.isRefreshing = true
                                textViewNoResults.isVisible = false

                                recyclerViewSearch.isVisible = !viewModel.newQueryInProgress && searchListItemAdapter.itemCount > 0

                                viewModel.refreshInProgress = true
                                viewModel.pendingScrollToTopAfterRefresh = true
                            }
                            is LoadState.NotLoading -> {
                                textViewError.isVisible = false
                                buttonRetry.isVisible = false
                                swipeRefreshLayout.isRefreshing = false
                                recyclerViewSearch.isVisible = searchListItemAdapter.itemCount > 0

                                val noResults =
                                    searchListItemAdapter.itemCount < 1 && loadState.append.endOfPaginationReached
                                            && loadState.source.append.endOfPaginationReached

                                textViewNoResults.isVisible = noResults

                                viewModel.refreshInProgress = false
                                viewModel.newQueryInProgress
                            }
                            is LoadState.Error -> {
                                swipeRefreshLayout.isRefreshing = false
                                textViewNoResults.isVisible = false
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
                                viewModel.newQueryInProgress = false
                                viewModel.pendingScrollToTopAfterRefresh = false
                            }
                            else -> {}
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
                        is SearchViewModel.Event.NavigateToDetailsFragment -> {
                            val action =
                                SearchFragmentDirections.actionSearchFragmentToMediaDetailsFragment(
                                    event.listItem
                                )
                            findNavController().navigate(action)
                        }
                        is SearchViewModel.Event.NavigateToPersonDetailsFragment -> {
                            val action =
                                SearchFragmentDirections.actionSearchFragmentToPersonDetailsFragment(
                                    event.id
                                )
                            findNavController().navigate(action)
                        }
                    }.exhaustive
                }
            }

            toolbar.apply {
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
                        return true
                    }
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewSearch.adapter = null
        currentBinding = null
    }
}
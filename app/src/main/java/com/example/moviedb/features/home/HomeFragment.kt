package com.example.moviedb.features.home

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviedb.R
import com.example.moviedb.databinding.FragmentHomeBinding
import com.example.moviedb.shared.ListItemAdapter
import com.example.moviedb.util.Resource
import com.example.moviedb.util.exhaustive
import com.example.moviedb.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHomeBinding.bind(view)

        val topMovieAdapter = ListItemAdapter(
            onItemClick = { movie ->

            },
            onWatchlistClick = { movie ->
                viewModel.onWatchlistClick(movie)
            }
        )

        val popularMovieAdapter = ListItemAdapter(
            onItemClick = { movie ->

            },
            onWatchlistClick = { movie ->
                viewModel.onWatchlistClick(movie)
            }
        )

        val topTvShowAdapter = ListItemAdapter(
            onItemClick = { tvShow ->

            },
            onWatchlistClick = { tvShow ->
                viewModel.onWatchlistClick(tvShow)
            }
        )

        binding.apply {
            recyclerViewTopMovies.apply {
                adapter = topMovieAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
            }

            recyclerViewPopularMovies.apply {
                adapter = popularMovieAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
            }

            recyclerViewTopTvShows.apply {
                adapter = topTvShowAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                launch {
                    viewModel.top20Movies.collect {
                        val result = it ?: return@collect

                        swipeRefreshLayout.isRefreshing = result is Resource.Loading
                        //recyclerViewTopMovies.isVisible = !result.data.isNullOrEmpty()
                        //recyclerViewPopularMovies.isVisible = !result.data.isNullOrEmpty()
                        //recyclerViewTopTvShows.isVisible = !result.data.isNullOrEmpty()
                        textViewError.isVisible =
                            result.error != null && result.data.isNullOrEmpty()
                        buttonRetry.isVisible =
                            result.error != null && result.data.isNullOrEmpty()
                        textViewError.text = getString(
                            R.string.could_not_refresh,
                            result.error?.localizedMessage
                                ?: getString(R.string.unknown_error_occurred)
                        )

                        topMovieAdapter.submitList(result.data) {
                            if (viewModel.pendingScrollToTopAfterRefresh) {
                                recyclerViewTopMovies.scrollToPosition(0)
                                viewModel.pendingScrollToTopAfterRefresh = false
                            }
                        }
                    }
                }

                launch {
                    viewModel.popular20Movies.collect {
                        val result = it ?: return@collect

                        swipeRefreshLayout.isRefreshing = result is Resource.Loading
                        //recyclerViewPopularMovies.isVisible = !result.data.isNullOrEmpty()
                        //recyclerViewTopMovies.isVisible = !result.data.isNullOrEmpty()
                        //recyclerViewTopTvShows.isVisible = !result.data.isNullOrEmpty()
                        textViewError.isVisible =
                            result.error != null && result.data.isNullOrEmpty()
                        buttonRetry.isVisible =
                            result.error != null && result.data.isNullOrEmpty()
                        textViewError.text = getString(
                            R.string.could_not_refresh,
                            result.error?.localizedMessage
                                ?: getString(R.string.unknown_error_occurred)
                        )

                        popularMovieAdapter.submitList(result.data) {
                            if (viewModel.pendingScrollToTopAfterRefresh) {
                                recyclerViewPopularMovies.scrollToPosition(0)
                                viewModel.pendingScrollToTopAfterRefresh = false
                            }
                        }
                    }
                }

                launch {
                    viewModel.top20TvShows.collect {
                        val result = it ?: return@collect

                        swipeRefreshLayout.isRefreshing = result is Resource.Loading
                        //recyclerViewPopularMovies.isVisible = !result.data.isNullOrEmpty()
                        //recyclerViewTopMovies.isVisible = !result.data.isNullOrEmpty()
                        //recyclerViewTopTvShows.isVisible = !result.data.isNullOrEmpty()
                        textViewError.isVisible =
                            result.error != null && result.data.isNullOrEmpty()
                        buttonRetry.isVisible =
                            result.error != null && result.data.isNullOrEmpty()
                        textViewError.text = getString(
                            R.string.could_not_refresh,
                            result.error?.localizedMessage
                                ?: getString(R.string.unknown_error_occurred)
                        )

                        topTvShowAdapter.submitList(result.data) {
                            if (viewModel.pendingScrollToTopAfterRefresh) {
                                recyclerViewTopTvShows.scrollToPosition(0)
                                viewModel.pendingScrollToTopAfterRefresh = false
                            }
                        }
                    }
                }
            }

            swipeRefreshLayout.setOnRefreshListener {
                viewModel.onManualRefresh()
            }

            buttonRetry.setOnClickListener {
                viewModel.onManualRefresh()
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.events.collect { event ->
                    when (event) {
                        is HomeViewModel.Event.ShowErrorMessage ->
                            showSnackbar(
                                getString(
                                    R.string.could_not_refresh,
                                    event.error.localizedMessage
                                        ?: getString(R.string.unknown_error_occurred)
                                )
                            )
                    }.exhaustive
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }
}
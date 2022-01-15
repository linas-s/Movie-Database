package com.example.moviedb.features.home

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviedb.MainActivity
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
class HomeFragment : Fragment(R.layout.fragment_home),
    MainActivity.OnBottomNavigationFragmentReselectedListener {

    private val viewModel: HomeViewModel by viewModels()

    private var currentBinding: FragmentHomeBinding? = null
    private val binding get() = currentBinding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentBinding = FragmentHomeBinding.bind(view)

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

        val popularTvShowAdapter = ListItemAdapter(
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
                itemAnimator?.changeDuration = 0
            }

            recyclerViewPopularMovies.apply {
                adapter = popularMovieAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
                itemAnimator?.changeDuration = 0
            }

            recyclerViewTopTvShows.apply {
                adapter = topTvShowAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
                itemAnimator?.changeDuration = 0
            }

            recyclerViewPopularTvShows.apply {
                adapter = popularTvShowAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
                itemAnimator?.changeDuration = 0
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                launch {
                    viewModel.top20Movies.collect {
                        val result = it ?: return@collect

                        swipeRefreshLayout.isRefreshing = result is Resource.Loading
                        recyclerViewTopMovies.isVisible = !result.data.isNullOrEmpty()
                        recyclerViewPopularMovies.isVisible = !result.data.isNullOrEmpty()
                        recyclerViewTopTvShows.isVisible = !result.data.isNullOrEmpty()
                        recyclerViewPopularTvShows.isVisible = !result.data.isNullOrEmpty()
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
                            if (viewModel.top20MoviesPendingScrollToTopAfterRefresh) {
                                recyclerViewTopMovies.scrollToPosition(0)
                                viewModel.top20MoviesPendingScrollToTopAfterRefresh = false
                            }
                        }
                    }
                }

                launch {
                    viewModel.popular20Movies.collect {
                        val result = it ?: return@collect

                        popularMovieAdapter.submitList(result.data) {
                            if (viewModel.popular20MoviesPendingScrollToTopAfterRefresh) {
                                recyclerViewPopularMovies.scrollToPosition(0)
                                viewModel.popular20MoviesPendingScrollToTopAfterRefresh = false
                            }
                        }
                    }
                }

                launch {
                    viewModel.top20TvShows.collect {
                        val result = it ?: return@collect

                        topTvShowAdapter.submitList(result.data) {
                            if (viewModel.top20TvShowsPendingScrollToTopAfterRefresh) {
                                recyclerViewTopTvShows.scrollToPosition(0)
                                viewModel.top20TvShowsPendingScrollToTopAfterRefresh = false
                            }
                        }
                    }
                }

                launch {
                    viewModel.popular20TvShows.collect {
                        val result = it ?: return@collect

                        popularTvShowAdapter.submitList(result.data) {
                            if (viewModel.popular20TvShowsPendingScrollToTopAfterRefresh) {
                                recyclerViewPopularTvShows.scrollToPosition(0)
                                viewModel.popular20TvShowsPendingScrollToTopAfterRefresh = false
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

    override fun onBottomNavigationFragmentReselected() {
        binding.scrollView.scrollTo(0, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentBinding = null
    }
}
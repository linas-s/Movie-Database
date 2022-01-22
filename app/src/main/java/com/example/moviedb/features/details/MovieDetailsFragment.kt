package com.example.moviedb.features.details

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.moviedb.R
import com.example.moviedb.databinding.FragmentMovieDetailsBinding
import com.example.moviedb.shared.CastCrewItemAdapter
import com.example.moviedb.shared.ListItemAdapter
import com.example.moviedb.util.Resource
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.graphics.Color
import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.navigation.fragment.findNavController
import com.example.moviedb.MainActivity


@AndroidEntryPoint
class MovieDetailsFragment : Fragment(R.layout.fragment_movie_details) {
    private val viewModel: MovieDetailsViewModel by viewModels()

    private var currentBinding: FragmentMovieDetailsBinding? = null
    private val binding get() = currentBinding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentBinding = FragmentMovieDetailsBinding.bind(view)

        val castAdapter = CastCrewItemAdapter(
            onItemClick = { cast ->

            }
        )

        val crewAdapter = CastCrewItemAdapter(
            onItemClick = { crew ->

            }
        )

        val recommendationAdapter = ListItemAdapter(
            onItemClick = {

            },
            onWatchlistClick = {

            }
        )

        val genreAdapter = GenreItemAdapter()

        binding.apply {

            toolbar.apply {
                setNavigationOnClickListener { findNavController().navigateUp() }
                setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            }

            appBarLayout.bringToFront()

            requireActivity().window.statusBarColor = Color.TRANSPARENT
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

            recyclerViewCast.apply {
                adapter = castAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
                itemAnimator?.changeDuration = 0
            }

            recyclerViewCrew.apply {
                adapter = crewAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
                itemAnimator?.changeDuration = 0
            }

            recyclerViewGenres.apply {
                adapter = genreAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                setHasFixedSize(true)
                itemAnimator?.changeDuration = 0
            }

            recyclerViewRecommended.apply {
                adapter = recommendationAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
                itemAnimator?.changeDuration = 0
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                launch {
                    viewModel.movie.collect {
                        val result = it ?: return@collect

                        progressBar.isVisible = result is Resource.Loading
                        tabLayout.isVisible = result is Resource.Success
                        imageViewBackdrop.isVisible = result is Resource.Success
                        imageViewPoster.isVisible = result is Resource.Success
                        textViewTitle.isVisible = result is Resource.Success
                        textViewReleaseDateRuntime.isVisible = result is Resource.Success
                        textViewTagline.isVisible = result is Resource.Success
                        textViewOverview.isVisible = result is Resource.Success
                        textViewDirectedBy.isVisible = result is Resource.Success
                        textViewDirector.isVisible = result is Resource.Success
                        textViewRating.isVisible = result is Resource.Success
                        textViewTrailer.isVisible = result is Resource.Success
                        textViewRecommended.isVisible = result is Resource.Success

                        Glide.with(imageViewBackdrop)
                            .load(result.data?.backdropUrl)
                            .into(imageViewBackdrop)

                        Glide.with(imageViewPoster)
                            .load(result.data?.posterUrl)
                            .into(imageViewPoster)

                        textViewTitle.text = result.data?.title ?: ""
                        toolbar.title = result.data?.title ?: ""
                        textViewReleaseDateRuntime.text =
                            result.data?.releaseYear + " · " + result.data?.runtime + " mins"

                        textViewOverview.text = result.data?.overview ?: ""
                        if(textViewOverview.layout != null) {
                            imageViewExpand.isVisible = textViewOverview.layout.getEllipsisCount(textViewOverview.lineCount - 1) > 0
                        }

                        textViewTagline.text = result.data?.tagline ?: ""
                        textViewRating.text = result.data?.voteAverage.toString()
                        textViewHomepage.text = result.data?.homepage ?: "No homepage available..."
                        textViewStatus.text = result.data?.status ?: "No status available..."
                        textViewBudget.text = result.data?.budget.toString()
                        textViewReleaseDate.text =
                            result.data?.releaseDate ?: "Unknown release date..."

                    }
                }

                launch {
                    viewModel.movieCast.collect {
                        val result = it ?: return@collect

                        recyclerViewCast.isVisible = result is Resource.Success

                        castAdapter.submitList(result.data)
                    }
                }

                launch {
                    viewModel.movieCrew.collect {
                        val result = it ?: return@collect

                        recyclerViewCrew.isVisible = result is Resource.Success

                        val movieDirectors = result.data?.filter { person ->
                            person.job == "Director"
                        }

                        textViewDirector.text = movieDirectors?.joinToString { director ->
                            director.title
                        }

                        crewAdapter.submitList(result.data)
                    }
                }

                launch {
                    viewModel.movieGenres.collect {
                        val result = it ?: return@collect

                        genreAdapter.submitList(result.data)
                    }
                }

                launch {
                    viewModel.movieRecommendations.collect {
                        val result = it ?: return@collect

                        recyclerViewRecommended.isVisible = result is Resource.Success

                        recommendationAdapter.submitList(result.data)
                    }
                }
            }

            viewOverviewContainer.setOnClickListener {
                when(textViewOverview.lineCount) {
                    3 -> {
                        val animatorSet = AnimatorSet()
                        val textViewObjectAnimator = ObjectAnimator.ofInt(textViewOverview, "maxLines", 25)
                        textViewObjectAnimator.duration = 200
                        val imageViewObjectAnimator = ObjectAnimator.ofFloat(imageViewExpand, View.ALPHA, 1f, 0f)
                        imageViewObjectAnimator.duration = 300
                        animatorSet.playTogether(textViewObjectAnimator, imageViewObjectAnimator)

                        animatorSet.start()
                    }
                    else -> {
                        val animatorSet = AnimatorSet()
                        val textViewObjectAnimator = ObjectAnimator.ofInt(textViewOverview, "maxLines", 3)
                        textViewObjectAnimator.duration = 200
                        val imageViewObjectAnimator = ObjectAnimator.ofFloat(imageViewExpand, View.ALPHA, 0f, 1f)
                        imageViewObjectAnimator.duration = 300
                        animatorSet.playTogether(textViewObjectAnimator, imageViewObjectAnimator)

                        animatorSet.start()
                    }
                }
            }

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val position = tab?.position
                    when (position) {
                        0 -> {
                            groupCastCrew.isVisible = true
                            groupDetails.isVisible = false
                            groupGenre.isVisible = false
                        }
                        1 -> {
                            groupCastCrew.isVisible = false
                            groupDetails.isVisible = true
                            groupGenre.isVisible = false
                        }
                        2 -> {
                            groupCastCrew.isVisible = false
                            groupDetails.isVisible = false
                            groupGenre.isVisible = true
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    true
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    true
                }
            })
        }
    }

}

package com.example.moviedb.features.details.media

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.moviedb.R
import com.example.moviedb.shared.CastCrewItemAdapter
import com.example.moviedb.shared.ListItemAdapter
import com.example.moviedb.util.Resource
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.core.graphics.ColorUtils
import java.text.NumberFormat
import android.content.Intent
import android.net.Uri
import androidx.core.view.isVisible
import com.example.moviedb.databinding.FragmentMediaDetailsBinding
import com.example.moviedb.util.exhaustive
import com.example.moviedb.util.showSnackbar
import com.google.android.material.snackbar.Snackbar


@AndroidEntryPoint
class MediaDetailsFragment : Fragment(R.layout.fragment_media_details) {
    private val viewModel: MediaDetailsViewModel by viewModels()

    private var currentBinding: FragmentMediaDetailsBinding? = null
    private val binding get() = currentBinding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentBinding = FragmentMediaDetailsBinding.bind(view)

        val castAdapter = CastCrewItemAdapter(
            onItemClick = { cast ->
                viewModel.onPersonClick(cast)
            }
        )

        val crewAdapter = CastCrewItemAdapter(
            onItemClick = { crew ->
                viewModel.onPersonClick(crew)
            }
        )

        val recommendationAdapter = ListItemAdapter(
            onItemClick = { item ->
                viewModel.onRecommendedListItemClick(item)
            },
            onWatchlistClick = { item ->
                viewModel.onWatchlistClick(item)
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
            requireActivity().window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

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
                    viewModel.media.collect {
                        val result = it ?: return@collect

                        progressBar.isVisible = result is Resource.Loading
                        tabLayout.isVisible = result is Resource.Success
                        imageViewBackdrop.isVisible = result is Resource.Success
                        imageViewPoster.isVisible = result is Resource.Success
                        textViewTitle.isVisible = result is Resource.Success
                        textViewReleaseDateRuntime.isVisible = result is Resource.Success
                        textViewTagline.isVisible = result is Resource.Success
                        textViewOverview.isVisible = result is Resource.Success
                        textViewDirectedCreatedBy.isVisible = result is Resource.Success
                        textViewDirector.isVisible = result is Resource.Success
                        textViewRating.isVisible = result is Resource.Success
                        textViewTrailer.isVisible = result is Resource.Success
                        imageViewStar.isVisible = result is Resource.Success


                        Glide.with(imageViewBackdrop)
                            .load(result.data?.backdropUrl)
                            .into(imageViewBackdrop)

                        Glide.with(imageViewPoster)
                            .load(result.data?.posterUrl)
                            .into(imageViewPoster)

                        textViewError.isVisible = result.error != null
                        buttonRetry.isVisible = result.error != null
                        textViewError.text = getString(
                            R.string.could_not_refresh,
                            result.error?.localizedMessage
                                ?: getString(R.string.unknown_error_occurred)
                        )

                        textViewTitle.text = result.data?.title ?: ""
                        toolbar.title = result.data?.title ?: ""
                        if (scrollView.scrollY == 0) toolbar.setTitleTextColor(Color.TRANSPARENT)


                        textViewOverview.text = result.data?.overview ?: ""
                        if (textViewOverview.layout != null && !imageViewExpand.isVisible) {
                            imageViewExpand.isVisible =
                                textViewOverview.layout.getEllipsisCount(textViewOverview.lineCount - 1) > 0 && result is Resource.Success
                        }

                        if (result.data?.mediaType == "movie") {
                            textViewDirectedCreatedBy.text = "DIRECTED BY"
                            textViewReleaseDateRuntime.text =
                                result.data?.releaseYear + " · " + result.data?.runtime + " mins"
                        } else {
                            textViewDirectedCreatedBy.text = "CREATED BY"
                            textViewReleaseDateRuntime.text =
                                result.data?.runningYears + " · " + result.data?.seasons
                        }

                        textViewTagline.text = result.data?.tagline ?: ""
                        textViewRating.text = result.data?.voteAverage.toString() + "/10"
                        textViewVoteCount.text =
                            NumberFormat.getInstance().format(result.data?.voteCount).toString()
                        textViewStatus.text = result.data?.status ?: "No status available..."
                        textViewBudget.text = result.data?.budgetText
                        textViewReleaseDate.text =
                            result.data?.releaseDate ?: "Unknown release date..."

                    }
                }

                launch {
                    viewModel.mediaCast.collect {
                        val result = it ?: return@collect

                        textViewCast.isVisible = result is Resource.Success
                        recyclerViewCast.isVisible = result is Resource.Success

                        castAdapter.submitList(result.data)
                    }
                }

                launch {
                    viewModel.mediaCrew.collect {
                        val result = it ?: return@collect

                        textViewCrew.isVisible = result is Resource.Success
                        recyclerViewCrew.isVisible = result is Resource.Success

                        val directorsCreators = viewModel.getDirectorsCreators(result.data)

                        textViewDirector.text = directorsCreators?.joinToString { director ->
                            director.title
                        }

                        crewAdapter.submitList(result.data)
                    }
                }

                launch {
                    viewModel.mediaGenres.collect {
                        val result = it ?: return@collect
                        genreAdapter.submitList(result.data)
                    }
                }

                launch {
                    viewModel.mediaRecommendations.collect {
                        val result = it ?: return@collect

                        textViewRecommended.isVisible = result is Resource.Success
                        recyclerViewRecommended.isVisible = result is Resource.Success

                        recommendationAdapter.submitList(result.data)
                    }
                }
            }

            textViewHomepageText.setOnClickListener {
                viewModel.onHomepageClick()
            }

            viewOverviewContainer.setOnClickListener {
                when (textViewOverview.lineCount) {
                    3 -> {
                        val animatorSet = AnimatorSet()
                        val textViewObjectAnimator =
                            ObjectAnimator.ofInt(textViewOverview, "maxLines", 25)
                        textViewObjectAnimator.duration = 100 * textViewOverview.lineCount.toLong()
                        val imageViewObjectAnimator =
                            ObjectAnimator.ofFloat(imageViewExpand, View.ALPHA, 1f, 0f)
                        imageViewObjectAnimator.duration = 300
                        animatorSet.playTogether(textViewObjectAnimator, imageViewObjectAnimator)

                        animatorSet.start()
                    }
                    else -> {
                        val animatorSet = AnimatorSet()
                        val textViewObjectAnimator =
                            ObjectAnimator.ofInt(textViewOverview, "maxLines", 3)
                        textViewObjectAnimator.duration = 300
                        val imageViewObjectAnimator =
                            ObjectAnimator.ofFloat(imageViewExpand, View.ALPHA, 0f, 1f)
                        imageViewObjectAnimator.duration = 300
                        animatorSet.playTogether(textViewObjectAnimator, imageViewObjectAnimator)

                        animatorSet.start()
                    }
                }
            }

            textViewTrailer.setOnClickListener {
                viewModel.onTrailerClick()
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

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }
            })

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                    when {
                        scrollY < 460 -> {
                            requireActivity().window.statusBarColor = Color.TRANSPARENT
                            toolbar.setBackgroundColor(Color.TRANSPARENT)
                            toolbar.setTitleTextColor(Color.TRANSPARENT)
                        }
                        scrollY < 545 -> {
                            requireActivity().window.statusBarColor = ColorUtils.setAlphaComponent(
                                Color.parseColor("#181b20"),
                                ((scrollY - 460) * 3)
                            )
                            toolbar.setBackgroundColor(
                                ColorUtils.setAlphaComponent(
                                    Color.parseColor(
                                        "#445565"
                                    ), ((scrollY - 460) * 3)
                                )
                            )
                            toolbar.setTitleTextColor(
                                ColorUtils.setAlphaComponent(
                                    Color.WHITE,
                                    (((scrollY - 460) * 3))
                                )
                            )
                        }
                        else -> {
                            requireActivity().window.statusBarColor = Color.parseColor("#181b20")
                            toolbar.setBackgroundColor(Color.parseColor("#445565"))
                            toolbar.setTitleTextColor(Color.WHITE)
                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.events.collect { event ->
                    when (event) {
                        is MediaDetailsViewModel.Event.NavigateToMediaDetailsFragment -> {
                            val action =
                                MediaDetailsFragmentDirections.actionMediaDetailsFragmentSelf(
                                    event.listItem
                                )
                            findNavController().navigate(action)
                        }
                        is MediaDetailsViewModel.Event.NavigateToPersonDetailsFragment -> {
                            val action =
                                MediaDetailsFragmentDirections.actionMediaDetailsFragmentToPersonDetailsFragment(
                                    event.id
                                )
                            findNavController().navigate(action)
                        }
                        is MediaDetailsViewModel.Event.OpenMediaTrailer -> {
                            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:${event.key}"))
                            startActivity(appIntent)
                        }
                        is MediaDetailsViewModel.Event.OpenMediaHomepage -> {
                            if (!event.url.isNullOrBlank()) {
                                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
                                startActivity(webIntent)
                            }
                            else {
                                showSnackbar(getString(R.string.no_homepage_available), duration = Snackbar.LENGTH_SHORT)
                            }
                        }
                    }.exhaustive
                }
            }
        }
    }

}

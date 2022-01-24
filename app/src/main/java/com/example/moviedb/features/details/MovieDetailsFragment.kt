package com.example.moviedb.features.details

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
import androidx.core.graphics.ColorUtils
import java.text.NumberFormat
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri


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
                        toolbar.setTitleTextColor(Color.TRANSPARENT)
                        textViewReleaseDateRuntime.text =
                            result.data?.releaseYear + " · " + result.data?.runtime + " mins"

                        textViewOverview.text = result.data?.overview ?: ""
                        if(textViewOverview.layout != null) {
                            imageViewExpand.isVisible = textViewOverview.layout.getEllipsisCount(textViewOverview.lineCount - 1) > 0 && result is Resource.Success
                        }

                        textViewTagline.text = result.data?.tagline ?: ""
                        textViewRating.text = result.data?.voteAverage.toString() + "/10"
                        textViewVoteCount.text = NumberFormat.getInstance().format(result.data?.voteCount).toString()
                        textViewHomepage.text = if(result.data?.homepage.isNullOrEmpty()) "No homepage available..." else result.data?.homepage
                        textViewStatus.text = result.data?.status ?: "No status available..."
                        textViewBudget.text = "$" + NumberFormat.getInstance().format(result.data?.budget ?: 0).toString()
                        textViewReleaseDate.text =
                            result.data?.releaseDate ?: "Unknown release date..."

                    }
                }

                launch {
                    viewModel.movieCast.collect {
                        val result = it ?: return@collect

                        textViewCast.isVisible = result is Resource.Success
                        recyclerViewCast.isVisible = result is Resource.Success

                        castAdapter.submitList(result.data)
                    }
                }

                launch {
                    viewModel.movieCrew.collect {
                        val result = it ?: return@collect

                        textViewCrew.isVisible = result is Resource.Success
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

                launch {
                    viewModel.movieVideo.collect {
                        val result = it ?: return@collect

                        textViewTrailer.setOnClickListener {
                            val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:${result.data?.key}"))
                            val webIntent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=${result.data?.key}")
                            )
                            try {
                                context!!.startActivity(appIntent)
                            } catch (ex: ActivityNotFoundException) {
                                context!!.startActivity(webIntent)
                            }
                        }
                    }
                }
            }

            viewOverviewContainer.setOnClickListener {
                when(textViewOverview.lineCount) {
                    3 -> {
                        val animatorSet = AnimatorSet()
                        val textViewObjectAnimator = ObjectAnimator.ofInt(textViewOverview, "maxLines", 10)
                        textViewObjectAnimator.duration = 100 * textViewOverview.lineCount.toLong()
                        val imageViewObjectAnimator = ObjectAnimator.ofFloat(imageViewExpand, View.ALPHA, 1f, 0f)
                        imageViewObjectAnimator.duration = 300
                        animatorSet.playTogether(textViewObjectAnimator, imageViewObjectAnimator)

                        animatorSet.start()
                    }
                    else -> {
                        val animatorSet = AnimatorSet()
                        val textViewObjectAnimator = ObjectAnimator.ofInt(textViewOverview, "maxLines", 3)
                        textViewObjectAnimator.duration = 300
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
                            requireActivity().window.statusBarColor = ColorUtils.setAlphaComponent(Color.parseColor("#181b20"), ((scrollY-460)*3))
                            toolbar.setBackgroundColor(ColorUtils.setAlphaComponent(Color.parseColor("#445565"), ((scrollY-460)*3)))
                            toolbar.setTitleTextColor(ColorUtils.setAlphaComponent(Color.WHITE, (((scrollY-460)*3))))
                        }
                        else -> {
                            requireActivity().window.statusBarColor = Color.parseColor("#181b20")
                            toolbar.setBackgroundColor(Color.parseColor("#445565"))
                            toolbar.setTitleTextColor(Color.WHITE)
                        }
                    }
                }
            }
        }
    }

}

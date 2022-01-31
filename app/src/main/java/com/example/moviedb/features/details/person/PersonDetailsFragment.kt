package com.example.moviedb.features.details.person

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.moviedb.R
import com.example.moviedb.databinding.FragmentPersonDetailsBinding
import com.example.moviedb.shared.ListItemAdapter
import com.example.moviedb.util.Resource
import com.example.moviedb.util.exhaustive
import com.example.moviedb.util.showSnackbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonDetailsFragment : Fragment(R.layout.fragment_person_details) {
    private val viewModel: PersonDetailsViewModel by viewModels()

    private var currentBinding: FragmentPersonDetailsBinding? = null
    private val binding get() = currentBinding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentBinding = FragmentPersonDetailsBinding.bind(view)

        requireActivity().window.statusBarColor = Color.parseColor("#445565")
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)

        val personCastAdapter = ListItemAdapter(
            onItemClick = { item ->
                viewModel.onItemListClick(item)
            },
            onWatchlistClick = { item ->
                viewModel.onWatchlistClick(item)
            }
        )

        val personCrewAdapter = ListItemAdapter(
            onItemClick = { item ->
                viewModel.onItemListClick(item)
            },
            onWatchlistClick = { item ->
                viewModel.onWatchlistClick(item)
            }
        )

        binding.apply {
            toolbar.apply {
                setNavigationOnClickListener { findNavController().navigateUp() }
                setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            }

            recyclerViewAsActor.apply {
                adapter = personCastAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
                itemAnimator?.changeDuration = 0
            }

            recyclerViewAsCrew.apply {
                adapter = personCrewAdapter
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                setHasFixedSize(true)
                itemAnimator?.changeDuration = 0
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                launch {
                    viewModel.personDetails.collect {
                        val result = it ?: return@collect

                        progressBar.isVisible = result is Resource.Loading
                        imageViewProfile.isVisible = result is Resource.Success
                        textViewBornText.isVisible = result is Resource.Success
                        textViewBirth.isVisible = result is Resource.Success
                        textViewKnownForText.isVisible = result is Resource.Success
                        textViewKnownFor.isVisible = result is Resource.Success
                        textViewHomepage.isVisible = result is Resource.Success
                        textViewBiography.isVisible = result is Resource.Success
                        textViewAsActorText.isVisible = result is Resource.Success
                        textViewAsCrewText.isVisible = result is Resource.Success
                        recyclerViewAsActor.isVisible = result is Resource.Success
                        recyclerViewAsCrew.isVisible = result is Resource.Success


                        toolbar.title = result.data?.title
                        textViewBirth.text =
                            (result.data?.birthday ?: "Unknown birthday") + " in " + (result.data?.placeOfBirth ?: "unknown birth location")
                        textViewKnownFor.text = result.data?.knownForDepartment
                        textViewBiography.text = result.data?.biography
                        Glide.with(imageViewProfile)
                            .load(result.data?.profileUrl)
                            .into(imageViewProfile)

                        textViewError.isVisible = result.error != null
                        buttonRetry.isVisible = result.error != null
                        textViewError.text = getString(
                            R.string.could_not_refresh,
                            result.error?.localizedMessage
                                ?: getString(R.string.unknown_error_occurred)
                        )

                        if (textViewBiography.layout != null && !imageViewExpand.isVisible) {
                            imageViewExpand.isVisible =
                                textViewBiography.layout.getEllipsisCount(textViewBiography.lineCount - 1) > 0 && result is Resource.Success
                        }
                    }
                }

                launch {
                    viewModel.personMediaCast.collect {
                        val result = it ?: return@collect

                        personCastAdapter.submitList(result.data?.distinct())
                    }
                }

                launch {
                    viewModel.personMediaCrew.collect {
                        val result = it ?: return@collect

                        personCrewAdapter.submitList(result.data?.distinct())
                    }
                }

                textViewHomepage.setOnClickListener {
                    viewModel.onHomepageClick()
                }

                viewOverviewContainer.setOnClickListener {
                    when (textViewBiography.lineCount) {
                        5 -> {
                            val animatorSet = AnimatorSet()
                            val textViewObjectAnimator =
                                ObjectAnimator.ofInt(textViewBiography, "maxLines", 50)
                            textViewObjectAnimator.duration =
                                100 * textViewBiography.lineCount.toLong()
                            val imageViewObjectAnimator =
                                ObjectAnimator.ofFloat(imageViewExpand, View.ALPHA, 1f, 0f)
                            imageViewObjectAnimator.duration = 300
                            animatorSet.playTogether(
                                textViewObjectAnimator,
                                imageViewObjectAnimator
                            )

                            animatorSet.start()
                        }
                        else -> {
                            val animatorSet = AnimatorSet()
                            val textViewObjectAnimator =
                                ObjectAnimator.ofInt(textViewBiography, "maxLines", 5)
                            textViewObjectAnimator.duration = 300
                            val imageViewObjectAnimator =
                                ObjectAnimator.ofFloat(imageViewExpand, View.ALPHA, 0f, 1f)
                            imageViewObjectAnimator.duration = 300
                            animatorSet.playTogether(
                                textViewObjectAnimator,
                                imageViewObjectAnimator
                            )

                            animatorSet.start()
                        }
                    }
                }

                buttonRetry.setOnClickListener {
                    viewModel.onRetryButtonClick()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is PersonDetailsViewModel.Event.NavigateToMediaDetailsFragment -> {
                        val action =
                            PersonDetailsFragmentDirections.actionPersonDetailsFragmentToMediaDetailsFragment(
                                event.listItem
                            )
                        findNavController().navigate(action)
                    }
                    is PersonDetailsViewModel.Event.OpenPersonHomepage -> {
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
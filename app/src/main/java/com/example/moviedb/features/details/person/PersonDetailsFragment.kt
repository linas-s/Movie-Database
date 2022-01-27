package com.example.moviedb.features.details.person

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.moviedb.R
import com.example.moviedb.databinding.FragmentPersonDetailsBinding
import com.example.moviedb.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonDetailsFragment: Fragment(R.layout.fragment_person_details) {
    private val viewModel: PersonDetailsViewModel by viewModels()

    private var currentBinding: FragmentPersonDetailsBinding? = null
    private val binding get() = currentBinding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentBinding = FragmentPersonDetailsBinding.bind(view)

        binding.apply {
            toolbar.apply {
                setNavigationOnClickListener { findNavController().navigateUp() }
                setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
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


                        toolbar.title = result.data?.title
                        textViewBirth.text = result.data?.birthday + " in " + result.data?.placeOfBirth
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

                        if(textViewBiography.layout != null && !imageViewExpand.isVisible) {
                            imageViewExpand.isVisible = textViewBiography.layout.getEllipsisCount(textViewBiography.lineCount - 1) > 0 && result is Resource.Success
                        }
                    }
                }

                viewOverviewContainer.setOnClickListener {
                    when(textViewBiography.lineCount) {
                        5 -> {
                            val animatorSet = AnimatorSet()
                            val textViewObjectAnimator = ObjectAnimator.ofInt(textViewBiography, "maxLines", 50)
                            textViewObjectAnimator.duration = 100 * textViewBiography.lineCount.toLong()
                            val imageViewObjectAnimator = ObjectAnimator.ofFloat(imageViewExpand, View.ALPHA, 1f, 0f)
                            imageViewObjectAnimator.duration = 300
                            animatorSet.playTogether(textViewObjectAnimator, imageViewObjectAnimator)

                            animatorSet.start()
                        }
                        else -> {
                            val animatorSet = AnimatorSet()
                            val textViewObjectAnimator = ObjectAnimator.ofInt(textViewBiography, "maxLines", 5)
                            textViewObjectAnimator.duration = 300
                            val imageViewObjectAnimator = ObjectAnimator.ofFloat(imageViewExpand, View.ALPHA, 0f, 1f)
                            imageViewObjectAnimator.duration = 300
                            animatorSet.playTogether(textViewObjectAnimator, imageViewObjectAnimator)

                            animatorSet.start()
                        }
                    }
                }

            }
        }
    }
}
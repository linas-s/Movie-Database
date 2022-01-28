package com.example.moviedb.shared

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.moviedb.data.CastCrewPerson
import com.example.moviedb.databinding.ItemCastCrewBinding

class CastCrewItemViewHolder(
    private val binding: ItemCastCrewBinding,
    private val onItemClick: (Int) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(castCrewPerson: CastCrewPerson){
        binding.apply {
            Glide.with(imageViewProfile)
                .load(castCrewPerson.profileUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageViewProfile)

            textViewPlaceholder.text = castCrewPerson.initials
            textViewPlaceholder.isVisible = castCrewPerson.posterPath.isNullOrEmpty()
            textViewName.text = castCrewPerson.title
            textViewJobCharacter.text = when (castCrewPerson.job){
                "acting" -> castCrewPerson.character
                else -> castCrewPerson.job
            }
        }
    }

    init {
        binding.apply {
            root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(position)
                }
            }
        }
    }
}
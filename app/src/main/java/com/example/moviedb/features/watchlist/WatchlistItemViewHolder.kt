package com.example.moviedb.features.watchlist

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.moviedb.R
import com.example.moviedb.data.ListItem
import com.example.moviedb.databinding.ItemWatchlistBinding

class WatchlistItemViewHolder (
    private val binding: ItemWatchlistBinding,
    private val onItemClick: (Int) -> Unit,
    private val onBookmarkClick: (Int) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(listItem: ListItem){
        binding.apply {
            Glide.with(itemView)
                .load(listItem.posterUrl)
                .error(R.drawable.ic_baseline_broken_image_24)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageViewPoster)

            textViewTitle.text = listItem.title
            textViewRating.text = listItem.voteAverageRounded
            textViewOverview.text = listItem.overview

            imageViewWatchlist.setImageResource(
                when{
                    listItem.isWatchlist -> R.drawable.ic_watchlist_selected
                    else -> R.drawable.ic_watchlist_unselected
                }
            )
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
            imageViewWatchlist.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onBookmarkClick(position)
                }
            }
        }
    }
}
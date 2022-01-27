package com.example.moviedb.features.search

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviedb.R
import com.example.moviedb.data.SearchListItem
import com.example.moviedb.databinding.ItemSearchListItemBinding

class SearchListItemViewHolder(
    private val binding: ItemSearchListItemBinding,
    private val onItemClick: (Int) -> Unit,
    private val onBookmarkClick: (Int) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(searchListItem: SearchListItem){
        binding.apply {
            Glide.with(itemView)
                .load(searchListItem.posterUrl)
                .error(R.drawable.ic_baseline_broken_image_24)
                .into(imageViewPoster)

            textViewTitle.text = searchListItem.title
            textViewMediaType.text = searchListItem.mediaType

            imageViewWatchlist.setImageResource(
                when (searchListItem.isWatchlist) {
                    true -> R.drawable.ic_watchlist_selected
                    false -> R.drawable.ic_watchlist_unselected
                    else -> {0}
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
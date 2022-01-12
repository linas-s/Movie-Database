package com.example.moviedb.shared

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviedb.R
import com.example.moviedb.data.ListItem
import com.example.moviedb.databinding.ItemListItemBinding

class ListItemViewHolder(
    private val binding: ItemListItemBinding,
    private val onItemClick: (Int) -> Unit,
    private val onBookmarkClick: (Int) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(listItem: ListItem){
        binding.apply {
            Glide.with(itemView)
                .load(listItem.posterUrl)
                .into(imageViewPoster)

            textViewTitle.text = listItem.title
            textViewRating.text = listItem.voteAverage.toString()

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
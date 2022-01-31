package com.example.moviedb.features.home

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.moviedb.R
import com.example.moviedb.data.ListItem
import com.example.moviedb.databinding.ItemViewPagerBinding

class ViewPagerViewHolder (
    private val binding: ItemViewPagerBinding,
    private val onItemClick: (Int) -> Unit,
    private val onBookmarkClick: (Int) -> Unit,
    private val onTrailerClick: (Int) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(listItem: ListItem){
            binding.apply {
                textViewTitle.text = listItem.title
                
                Glide.with(imageViewPoster)
                    .load(listItem.posterUrl)
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageViewPoster)

                Glide.with(imageViewBackdrop)
                    .load(listItem.backdropUrl)
                    .error(R.drawable.ic_baseline_broken_image_24)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageViewBackdrop)

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
                imageViewPlayTrailer.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onTrailerClick(position)
                    }
                }
            }
        }
    }
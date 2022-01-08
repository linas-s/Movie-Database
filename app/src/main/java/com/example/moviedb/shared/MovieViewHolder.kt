package com.example.moviedb.shared

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviedb.R
import com.example.moviedb.data.Movie
import com.example.moviedb.databinding.ItemMovieBinding

class MovieViewHolder(
    private val binding: ItemMovieBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: Movie){
        binding.apply {
            Glide.with(itemView)
                .load(movie.posterUrl)
                .error(R.drawable.image_placeholder)
                .into(imageViewPoster)

            textViewTitle.text = movie.title
            textViewRating.text = movie.voteAverage.toString()

            imageViewWatchlist.setImageResource(
                when{
                    movie.isWatchlist -> R.drawable.ic_watchlist_selected
                    else -> R.drawable.ic_watchlist_unselected
                }
            )
        }
    }
}
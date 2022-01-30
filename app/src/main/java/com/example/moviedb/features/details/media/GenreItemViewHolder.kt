package com.example.moviedb.features.details.media

import androidx.recyclerview.widget.RecyclerView
import com.example.moviedb.data.MediaGenre
import com.example.moviedb.databinding.ItemGenreBinding

class GenreItemViewHolder(private val binding: ItemGenreBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(mediaGenre: MediaGenre){
        binding.apply {
            textViewGenre.text = mediaGenre.genreName
        }
    }
}
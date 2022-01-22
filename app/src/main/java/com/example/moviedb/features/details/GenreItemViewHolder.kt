package com.example.moviedb.features.details

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moviedb.data.CastCrewPerson
import com.example.moviedb.data.MediaGenre
import com.example.moviedb.databinding.ItemCastCrewBinding
import com.example.moviedb.databinding.ItemGenreBinding

class GenreItemViewHolder(private val binding: ItemGenreBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(mediaGenre: MediaGenre){
        binding.apply {
            textViewGenre.text = mediaGenre.genreName
        }
    }
}
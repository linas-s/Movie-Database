package com.example.moviedb.features.details

import androidx.recyclerview.widget.DiffUtil
import com.example.moviedb.data.CastCrewPerson
import com.example.moviedb.data.MediaGenre

class GenreItemComparator : DiffUtil.ItemCallback<MediaGenre>() {
    override fun areItemsTheSame(oldItem: MediaGenre, newItem: MediaGenre) =
        oldItem.genreId == newItem.genreId

    override fun areContentsTheSame(oldItem: MediaGenre, newItem: MediaGenre) =
        oldItem == newItem
}
package com.example.moviedb.features.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.moviedb.data.CastCrewPerson
import com.example.moviedb.data.MediaGenre
import com.example.moviedb.databinding.ItemCastCrewBinding
import com.example.moviedb.databinding.ItemGenreBinding

class GenreItemAdapter() : ListAdapter<MediaGenre, GenreItemViewHolder>(GenreItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreItemViewHolder {
        val binding =
            ItemGenreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GenreItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenreItemViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }
}
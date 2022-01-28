package com.example.moviedb.features.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.moviedb.data.ListItem
import com.example.moviedb.databinding.ItemViewPagerBinding

class ViewPagerAdapter (
    private val onItemClick: (ListItem) -> Unit,
    private val onWatchlistClick: (ListItem) -> Unit,
    private val onTrailerClick: (ListItem) -> Unit
) : ListAdapter<ListItem, ViewPagerViewHolder>(ViewPagerComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val binding =
            ItemViewPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewPagerViewHolder(binding,
            onItemClick = { position ->
                val listItem = getItem(position)
                if (listItem != null) {
                    onItemClick(listItem)
                }
            },
            onBookmarkClick = { position ->
                val listItem = getItem(position)
                if (listItem != null) {
                    onWatchlistClick(listItem)
                }
            },
            onTrailerClick = { position ->
                val listItem = getItem(position)
                if (listItem != null) {
                    onTrailerClick(listItem)
                }
            }
        )
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }
}
package com.example.moviedb.features.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.moviedb.data.ListItem
import com.example.moviedb.databinding.ItemWatchlistBinding
import com.example.moviedb.shared.ListItemComparator

class WatchlistItemAdapter (
    private val onItemClick: (ListItem) -> Unit,
    private val onWatchlistClick: (ListItem) -> Unit
) : ListAdapter<ListItem, WatchlistItemViewHolder>(ListItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchlistItemViewHolder {
        val binding =
            ItemWatchlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WatchlistItemViewHolder(binding,
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
            }
        )
    }

    override fun onBindViewHolder(holder: WatchlistItemViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }
}
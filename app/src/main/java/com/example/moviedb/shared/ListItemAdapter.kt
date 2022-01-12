package com.example.moviedb.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.moviedb.data.ListItem
import com.example.moviedb.databinding.ItemListItemBinding

class ListItemAdapter(
    private val onItemClick: (ListItem) -> Unit,
    private val onWatchlistClick: (ListItem) -> Unit
) : ListAdapter<ListItem, ListItemViewHolder>(ListItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val binding =
            ItemListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListItemViewHolder(binding,
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

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }
}
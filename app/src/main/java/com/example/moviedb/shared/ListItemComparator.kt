package com.example.moviedb.shared

import androidx.recyclerview.widget.DiffUtil
import com.example.moviedb.data.ListItem

class ListItemComparator : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem) =
        oldItem.id == newItem.id && oldItem.mediaType == newItem.mediaType

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem) =
        oldItem == newItem
}
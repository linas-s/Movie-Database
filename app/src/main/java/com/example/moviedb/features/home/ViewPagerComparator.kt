package com.example.moviedb.features.home

import androidx.recyclerview.widget.DiffUtil
import com.example.moviedb.data.ListItem

class ViewPagerComparator  : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem) =
        oldItem == newItem
}
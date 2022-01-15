package com.example.moviedb.features.search

import androidx.recyclerview.widget.DiffUtil
import com.example.moviedb.data.SearchListItem

class SearchListItemComparator : DiffUtil.ItemCallback<SearchListItem>() {
    override fun areItemsTheSame(oldItem: SearchListItem, newItem: SearchListItem) =
        oldItem.id == newItem.id && oldItem.mediaType == newItem.mediaType

    override fun areContentsTheSame(oldItem: SearchListItem, newItem: SearchListItem) =
        oldItem == newItem
}
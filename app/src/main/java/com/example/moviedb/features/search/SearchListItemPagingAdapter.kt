package com.example.moviedb.features.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import com.example.moviedb.data.SearchListItem
import com.example.moviedb.databinding.ItemSearchListItemBinding

class SearchListItemPagingAdapter(
    private val onItemClick: (SearchListItem) -> Unit,
    private val onWatchlistClick: (SearchListItem) -> Unit
) : PagingDataAdapter<SearchListItem, SearchListItemViewHolder>(SearchListItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListItemViewHolder {
        val binding =
            ItemSearchListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchListItemViewHolder(binding,
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

    override fun onBindViewHolder(holder: SearchListItemViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }
}
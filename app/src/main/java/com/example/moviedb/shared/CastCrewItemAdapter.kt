package com.example.moviedb.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.moviedb.data.CastCrewPerson
import com.example.moviedb.databinding.ItemCastCrewBinding

class CastCrewItemAdapter(
    private val onItemClick: (CastCrewPerson) -> Unit
) : ListAdapter<CastCrewPerson, CastCrewItemViewHolder>(CastCrewItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastCrewItemViewHolder {
        val binding =
            ItemCastCrewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CastCrewItemViewHolder(binding,
            onItemClick = { position ->
                val castCrewPerson = getItem(position)
                if (castCrewPerson != null) {
                    onItemClick(castCrewPerson)
                }
            }
        )
    }

    override fun onBindViewHolder(holder: CastCrewItemViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }
}
package com.example.moviedb.shared

import androidx.recyclerview.widget.DiffUtil
import com.example.moviedb.data.CastCrewPerson

class CastCrewItemComparator : DiffUtil.ItemCallback<CastCrewPerson>() {
    override fun areItemsTheSame(oldItem: CastCrewPerson, newItem: CastCrewPerson) =
        oldItem.id == newItem.id && oldItem.job == newItem.job

    override fun areContentsTheSame(oldItem: CastCrewPerson, newItem: CastCrewPerson) =
        oldItem == newItem
}
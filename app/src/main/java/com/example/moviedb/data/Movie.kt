package com.example.moviedb.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "movie")
data class Movie(
    @PrimaryKey val id: Int,
    val title: String,
    val releaseDate: String,
    val popularity: Double,
    val voteAverage: Double,
    val voteCount: Int,
    val overview: String?,
    val backdropPath: String?,
    val posterPath: String?,
    val homepage: String?,
    val runtime: Int?,
    val isWatchlist: Boolean,
    val updatedAt: Long = System.currentTimeMillis()
){
    val posterUrl: String get() = "https://image.tmdb.org/t/p/w500$posterPath"
    val backdropUrl: String get() = "https://image.tmdb.org/t/p/w500$backdropPath"
}

@Entity(tableName = "tv_show")
data class TvShow(
    @PrimaryKey val id: Int,
    val title: String,
    val releaseDate: String,
    val lastAirDate: String?,
    val status: String?,
    val popularity: Double,
    val voteAverage: Double,
    val voteCount: Int,
    val overview: String,
    val backdropPath: String?,
    val posterPath: String?,
    val homepage: String?,
    val isWatchlist: Boolean,
    val updatedAt: Long = System.currentTimeMillis()
){
    val posterUrl: String get() = "https://image.tmdb.org/t/p/w500$posterPath"
    val backdropUrl: String get() = "https://image.tmdb.org/t/p/w500$backdropPath"
}

data class ListItem(
    val id: Int,
    val title: String,
    val releaseDate: String,
    val posterPath: String?,
    val voteAverage: Double,
    val isWatchlist: Boolean,
    val updatedAt: Long,
    val mediaType: String
){
    val posterUrl: String get() = "https://image.tmdb.org/t/p/w500$posterPath"
}
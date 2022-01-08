package com.example.moviedb.api

data class MovieDto(
    val id: Int,
    val title: String,
    val release_date: String,
    val popularity: Double,
    val vote_average: Double,
    val vote_count: Int,
    val overview: String?,
    val backdrop_path: String?,
    val poster_path: String?,
    val homepage: String?,
    val runtime: Int?
)

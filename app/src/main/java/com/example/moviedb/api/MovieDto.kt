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

data class TvShowDto(
    val id: Int,
    val name: String,
    val first_air_date: String,
    val last_air_date: String,
    val status: String?,
    val popularity: Double,
    val vote_average: Double,
    val vote_count: Int,
    val overview: String,
    val backdrop_path: String?,
    val poster_path: String?,
    val homepage: String?
)

data class SearchDto(
    val id: Int,
    val media_type: String,
    val popularity: Double,
    val overview: String?,
    val poster_path: String?,
    val backdrop_path: String?,
    val vote_average: Double?,
    val vote_count: Int?,
    val name: String?,
    val profile_path: String?,
    val release_date: String?,
    val title: String?,
    val known_for_department: String?,
    val first_air_date: String?
)
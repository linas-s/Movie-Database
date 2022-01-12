package com.example.moviedb.api

data class MovieListResponse(
    val results: List<MovieDto>
)

data class TvShowsListResponse(
    val results: List<TvShowDto>
)
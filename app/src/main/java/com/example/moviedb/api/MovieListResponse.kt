package com.example.moviedb.api

data class MovieListResponse(
    val results: List<MovieDto>
)

data class TvShowsListResponse(
    val results: List<TvShowDto>
)

data class SearchListResponse(
    val results: List<SearchDto>
)

data class CreditsResponse(
    val cast: List<CastCrewDto>,
    val crew: List<CastCrewDto>
)
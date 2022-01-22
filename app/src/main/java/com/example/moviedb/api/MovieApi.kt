package com.example.moviedb.api

import com.example.moviedb.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val API_KEY = BuildConfig.MOVIE_API_ACCESS_KEY
    }

    @GET("movie/top_rated")
    suspend fun getTopMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("page") page: Int
    ): MovieListResponse

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("page") page: Int
    ): MovieListResponse

    @GET("tv/top_rated")
    suspend fun getTopTvShows(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("page") page: Int
    ): TvShowsListResponse

    @GET("tv/popular")
    suspend fun getPopularTvShows(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("page") page: Int
    ): TvShowsListResponse

    @GET("search/multi")
    suspend fun searchMedia(
        @Query("api_key") apiKey: String = API_KEY,
        @Query("query") query: String,
        @Query("page") page: Int
    ): SearchListResponse

    @GET("movie/{id}/credits")
    suspend fun getMovieCredits(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String = API_KEY,
    ): CreditsResponse

    @GET("movie/{id}")
    suspend fun getMovieDetails(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String = API_KEY
    ): MovieDetailsDto

    @GET("movie/{id}/recommendations")
    suspend fun getRecommendedMovies(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("page") page: Int
    ): MovieListResponse
}
package com.example.moviedb.api

import com.example.moviedb.BuildConfig
import retrofit2.http.GET
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
}
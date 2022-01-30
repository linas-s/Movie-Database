package com.example.moviedb.api

import com.example.moviedb.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val API_KEY = BuildConfig.MOVIE_API_ACCESS_KEY
        const val TRENDING_MEDIA_TYPE = "movie"
        const val TRENDING_TIME_WINDOW = "week"
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

    @GET("tv/{tv_id}/recommendations\n")
    suspend fun getRecommendedTvShows(
        @Path("tv_id") id: Int,
        @Query("api_key") apiKey: String = API_KEY,
        @Query("page") page: Int
    ): TvShowsListResponse

    @GET("movie/{id}/videos")
    suspend fun getMovieVideos(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String = API_KEY
    ): MediaVideoResponse

    @GET("tv/{tv_id}/videos")
    suspend fun getTvShowVideos(
        @Path("tv_id") id: Int,
        @Query("api_key") apiKey: String = API_KEY
    ): MediaVideoResponse

    @GET("tv/{tv_id}")
    suspend fun getTvShowDetails(
        @Path("tv_id") id: Int,
        @Query("api_key") apiKey: String = API_KEY
    ): TvShowDetailsDto

    @GET("tv/{tv_id}/credits")
    suspend fun getTvShowCredits(
        @Path("tv_id") id: Int,
        @Query("api_key") apiKey: String = API_KEY,
    ): CreditsResponse

    @GET("person/{person_id}")
    suspend fun getPersonDetails(
        @Path("person_id") id: Int,
        @Query("api_key") apiKey: String = API_KEY
    ): PersonDetailsDto

    @GET("person/{person_id}/combined_credits")
    suspend fun getPersonMedia(
        @Path("person_id") id: Int,
        @Query("api_key") apiKey: String = API_KEY
    ): PersonMediaResponse

    @GET("trending/{media_type}/{time_window}")
    suspend fun getTrendingMovies(
        @Path("media_type") mediaType: String = TRENDING_MEDIA_TYPE,
        @Path("time_window") timeWindow: String = TRENDING_TIME_WINDOW,
        @Query("api_key") apiKey: String = API_KEY
    ):MovieListResponse
}
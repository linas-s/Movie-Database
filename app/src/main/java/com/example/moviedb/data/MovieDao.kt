package com.example.moviedb.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM movie WHERE id = :id")
    suspend fun getMovie(id: Int): Movie

    @Query("SELECT * FROM tv_show WHERE id = :id")
    suspend fun getTvShow(id: Int): TvShow

    @Query("SELECT id, title, releaseDate, posterPath, voteAverage, isWatchlist, updatedAt, 'movie' AS mediaType FROM movie ORDER BY voteAverage DESC LIMIT 20")
    fun getTop20Movies(): Flow<List<ListItem>>

    @Query("SELECT id, title, releaseDate, posterPath, voteAverage, isWatchlist, updatedAt, 'movie' AS mediaType FROM movie ORDER BY popularity DESC LIMIT 20")
    fun getPopular20Movies(): Flow<List<ListItem>>

    @Query("SELECT id, title, releaseDate, posterPath, voteAverage, isWatchlist, updatedAt, 'tv' AS mediaType FROM tv_show ORDER BY voteAverage DESC LIMIT 20")
    fun getTop20TvShows(): Flow<List<ListItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvShows(tvShowShows: List<TvShow>)

    @Update
    suspend fun updateMovie(movie: Movie)

    @Update
    suspend fun updateTvShow(tvShow: TvShow)

    @Query("DELETE from movie WHERE updatedAt < :timestampInMillis AND isWatchlist = 0")
    suspend fun deleteNonWatchlistMoviesOlderThan(timestampInMillis: Long)

    @Query("DELETE from tv_show WHERE updatedAt < :timestampInMillis AND isWatchlist = 0")
    suspend fun deleteNonWatchlistTvShowsOlderThan(timestampInMillis: Long)
}
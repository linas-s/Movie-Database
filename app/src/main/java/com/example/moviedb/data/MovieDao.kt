package com.example.moviedb.data

import androidx.paging.PagingSource
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

    @Query("SELECT id, title, releaseDate, posterPath, voteAverage, isWatchlist, updatedAt, 'tv' AS mediaType FROM tv_show ORDER BY popularity DESC LIMIT 20")
    fun getPopular20TvShows(): Flow<List<ListItem>>

    @Query("SELECT id, title, releaseDate, posterPath, voteAverage, isWatchlist, updatedAt, 'movie' AS mediaType FROM movie WHERE isWatchlist = 1")
    fun getAllWatchlistMovies(): Flow<List<ListItem>>

    @Query("SELECT id, title, releaseDate, posterPath, voteAverage, isWatchlist, updatedAt, 'tv' AS mediaType FROM tv_show WHERE isWatchlist = 1")
    fun getAllWatchlistTvShows(): Flow<List<ListItem>>

    @Query(
        "SELECT id, title, releaseDate, posterPath, voteAverage, isWatchlist, updatedAt, 'movie' AS mediaType FROM movie WHERE isWatchlist = 1 UNION " +
                "SELECT id, title, releaseDate, posterPath, voteAverage, isWatchlist, updatedAt, 'tv' AS mediaType FROM tv_show WHERE isWatchlist = 1"
    )
    fun getAllWatchlistMedia(): Flow<List<ListItem>>

    @Query("SELECT * FROM (SELECT id, title, posterPath, isWatchlist, 'movie' AS mediaType FROM movie UNION SELECT id, title, posterPath, isWatchlist, 'tv' as mediaType FROM tv_show UNION SELECT id, title, posterPath, NULL as isWatchlist, 'person' as mediaType FROM person) AS m INNER JOIN search_results AS s ON m.id = s.id AND m.mediaType = s.mediaType WHERE searchQuery = :query ORDER BY queryPosition")
    fun getSearchResultMediaPaged(query: String): PagingSource<Int, SearchListItem>

    @Query("SELECT MAX(queryPosition) FROM search_results WHERE searchQuery = :searchQuery")
    suspend fun getLastQueryPosition(searchQuery: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvShows(tvShowShows: List<TvShow>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersons(persons: List<Person>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchResults(searchResults: List<SearchResult>)

    @Update
    suspend fun updateMovie(movie: Movie)

    @Update
    suspend fun updateTvShow(tvShow: TvShow)

    @Update
    suspend fun updatePerson(person: Person)

    @Query("UPDATE movie SET isWatchlist = 0")
    suspend fun resetAllMovieWatchlist()

    @Query("UPDATE tv_show SET isWatchlist = 0")
    suspend fun resetAllTvShowWatchlist()

    @Query("DELETE FROM search_results WHERE searchQuery = :query")
    suspend fun deleteSearchResultsForQuery(query: String)

    @Query("DELETE from movie WHERE updatedAt < :timestampInMillis AND isWatchlist = 0")
    suspend fun deleteNonWatchlistMoviesOlderThan(timestampInMillis: Long)

    @Query("DELETE from tv_show WHERE updatedAt < :timestampInMillis AND isWatchlist = 0")
    suspend fun deleteNonWatchlistTvShowsOlderThan(timestampInMillis: Long)
}
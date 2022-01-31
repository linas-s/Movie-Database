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

    @Query("SELECT * FROM person WHERE id = :id")
    fun getPerson(id: Int): Flow<Person>

    @Query("SELECT id, title, tagline, releaseDate, lastAirDate, popularity, voteCount, voteAverage, overview, backdropPath, " +
            "posterPath, homepage, status, null as runtime, null as budget, numberOfSeasons, 'tv_show' as mediaType " +
            "FROM tv_show " +
            "WHERE id = :id")
    fun getTvShowFlow(id: Int): Flow<MediaDetails>

    @Query("SELECT id, title, tagline, releaseDate, popularity, voteCount, voteAverage, overview, backdropPath, " +
            "posterPath, homepage, status, budget, runtime, null as numberOfSeasons, null as lastAirDate, 'movie' as mediaType " +
            "FROM movie " +
            "WHERE id = :id")
    fun getMovieFlow(id: Int): Flow<MediaDetails>

    @Query("SELECT id, title, releaseDate, overview, popularity, posterPath, backdropPath, voteAverage, isWatchlist, " +
            "updatedAt, 'movie' AS mediaType " +
            "FROM movie " +
            "WHERE voteCount > 5000 " +
            "ORDER BY voteAverage DESC, popularity DESC " +
            "LIMIT 20")
    fun getTop20Movies(): Flow<List<ListItem>>

    @Query("SELECT id, title, releaseDate, overview, popularity, posterPath, backdropPath, voteAverage, isWatchlist, " +
            "updatedAt, 'movie' AS mediaType " +
            "FROM movie " +
            "ORDER BY popularity DESC " +
            "LIMIT 20")
    fun getPopular20Movies(): Flow<List<ListItem>>

    @Query("SELECT id, title, releaseDate, overview, posterPath, backdropPath, popularity, voteAverage, isWatchlist, " +
            "updatedAt, 'tv' AS mediaType " +
            "FROM tv_show WHERE voteCount > 1000 " +
            "ORDER BY voteAverage DESC, popularity DESC " +
            "LIMIT 20")
    fun getTop20TvShows(): Flow<List<ListItem>>

    @Query("SELECT id, title, releaseDate, overview, posterPath, backdropPath, popularity, voteAverage, isWatchlist, " +
            "updatedAt, 'tv' AS mediaType " +
            "FROM tv_show " +
            "ORDER BY popularity DESC " +
            "LIMIT 20")
    fun getPopular20TvShows(): Flow<List<ListItem>>

    @Query("SELECT m.id, m.title, m.releaseDate, m.overview, m.popularity, m.posterPath, m.backdropPath, m.voteAverage, m.isWatchlist, " +
            "m.updatedAt, 'movie' AS mediaType " +
            "FROM movie m " +
            "INNER JOIN trending t " +
            "ON m.id = t.mediaId")
    fun getTrendingMovies(): Flow<List<ListItem>>

    @Query("SELECT id, title, releaseDate, posterPath, overview, backdropPath, popularity, voteAverage, isWatchlist, " +
            "updatedAt, 'movie' AS mediaType " +
            "FROM movie " +
            "WHERE isWatchlist = 1")
    fun getAllWatchlistMovies(): Flow<List<ListItem>>

    @Query("SELECT id, title, releaseDate, posterPath, overview, backdropPath, popularity, voteAverage, " +
            "isWatchlist, updatedAt, 'tv' AS mediaType " +
            "FROM tv_show " +
            "WHERE isWatchlist = 1")
    fun getAllWatchlistTvShows(): Flow<List<ListItem>>

    @Query(
        "SELECT id, title, releaseDate, posterPath, overview, backdropPath, voteAverage, popularity, isWatchlist, updatedAt, 'movie' AS mediaType " +
                "FROM movie " +
                "WHERE isWatchlist = 1 " +
                "UNION " +
                "SELECT id, title, releaseDate, posterPath, overview, backdropPath, voteAverage, popularity, isWatchlist, updatedAt, 'tv' AS mediaType " +
                "FROM tv_show " +
                "WHERE isWatchlist = 1"
    )
    fun getAllWatchlistMedia(): Flow<List<ListItem>>

    @Query("SELECT m.id, m.mediaType, m.title, m.posterPath, m.isWatchlist FROM " +
            "(SELECT id, title, posterPath, isWatchlist, 'movie' AS mediaType " +
            "FROM movie " +
            "UNION " +
            "SELECT id, title, posterPath, isWatchlist, 'tv' as mediaType " +
            "FROM tv_show " +
            "UNION SELECT id, title, posterPath, NULL as isWatchlist, 'person' as mediaType " +
            "FROM person) " +
            "AS m " +
            "INNER JOIN search_results AS s " +
            "ON m.id = s.id AND m.mediaType = s.mediaType " +
            "WHERE searchQuery = :query " +
            "ORDER BY queryPosition")
    fun getSearchResultMediaPaged(query: String): PagingSource<Int, SearchListItem>

    @Query("SELECT MAX(queryPosition) FROM search_results WHERE searchQuery = :searchQuery")
    suspend fun getLastQueryPosition(searchQuery: String): Int?

    @Query(
        "SELECT p.id, p.title, p.posterPath, p.popularity, c.character, c.job, c.listOrder FROM Person p\n" +
                "INNER JOIN\n" +
                "Credits c on p.id = c.personId\n" +
                "WHERE c.mediaId = :movieId AND c.mediaType = 'movie' AND c.character IS NOT NULL \n" +
                "ORDER BY listOrder"
    )
    fun getMovieCast(movieId: Int): Flow<List<CastCrewPerson>>

    @Query(
        "SELECT p.id, p.title, p.posterPath, p.popularity, c.character, c.job, c.listOrder FROM Person p\n" +
                "INNER JOIN\n" +
                "Credits c on p.id = c.personId\n" +
                "WHERE c.mediaId = :tvShowId AND c.mediaType = 'tv' AND c.character IS NOT NULL \n" +
                "ORDER BY listOrder"
    )
    fun getTvShowCast(tvShowId: Int): Flow<List<CastCrewPerson>>

    @Query(
        "SELECT p.id, p.title, p.posterPath, p.popularity, c.character, c.job, c.listOrder FROM Person p\n" +
                "INNER JOIN\n" +
                "Credits c on p.id = c.personId\n" +
                "WHERE c.mediaId = :movieId AND c.mediaType = 'movie' AND c.character IS NULL \n" +
                "ORDER BY popularity DESC"
    )
    fun getMovieCrew(movieId: Int): Flow<List<CastCrewPerson>>

    @Query(
        "SELECT p.id, p.title, p.posterPath, p.popularity, c.character, c.job, c.listOrder FROM Person p\n" +
                "INNER JOIN\n" +
                "Credits c on p.id = c.personId\n" +
                "WHERE c.mediaId = :tvShowId AND c.mediaType = 'tv' AND c.character IS NULL \n" +
                "ORDER BY popularity DESC"
    )
    fun getTvShowCrew(tvShowId: Int): Flow<List<CastCrewPerson>>

    @Query("SELECT m.id, m.title, m.releaseDate, m.overview, m.posterPath, m.voteAverage, m.isWatchlist, m.backdropPath, m.popularity, m.updatedAt, m.mediaType FROM " +
            "(SELECT id, title, popularity, releaseDate, overview, posterPath, backdropPath, voteAverage, isWatchlist, updatedAt, 'movie' AS mediaType " +
            "FROM movie " +
            "UNION " +
            "SELECT id, title, popularity, releaseDate, overview, posterPath, backdropPath, voteAverage, isWatchlist, updatedAt, 'tv' AS mediaType " +
            "FROM tv_show) as m " +
            "INNER JOIN credits c " +
            "ON m.mediaType = c.mediaType AND m.id = c.mediaId AND c.personId = :personId " +
            "WHERE c.job = 'acting' " +
            "ORDER BY popularity DESC")
    fun getPersonMediaCast(personId: Int): Flow<List<ListItem>>

    @Query("SELECT m.id, m.title, m.releaseDate, m.overview, m.posterPath, m.voteAverage, m.isWatchlist, m.backdropPath, m.popularity, m.updatedAt, m.mediaType FROM " +
            "(SELECT id, title, popularity, releaseDate, overview, posterPath, backdropPath, voteAverage, isWatchlist, updatedAt, 'movie' AS mediaType " +
            "FROM movie " +
            "UNION " +
            "SELECT id, title, popularity, releaseDate, overview, posterPath, backdropPath, voteAverage, isWatchlist, updatedAt, 'tv' AS mediaType " +
            "FROM tv_show) as m " +
            "INNER JOIN credits c ON m.mediaType = c.mediaType AND m.id = c.mediaId AND c.personId = :personId " +
            "WHERE c.job != 'acting' " +
            "ORDER BY popularity DESC")
    fun getPersonMediaCrew(personId: Int): Flow<List<ListItem>>

    @Query("SELECT m.id, m.title, m.overview, m.popularity, m.releaseDate, m.posterPath, m.backdropPath, " +
            "m.voteAverage, m.isWatchlist, m.updatedAt, 'movie' AS mediaType " +
            "FROM movie m " +
            "INNER JOIN media_recommendation r " +
            "ON r.recommendedId = m.id " +
            "WHERE r.mediaType = 'movie' AND r.id = :movieId")
    fun getRecommendedMovies(movieId: Int): Flow<List<ListItem>>

    @Query("SELECT t.id, t.title, t.overview, t.popularity, t.releaseDate, t.posterPath, t.backdropPath, " +
            "t.voteAverage, t.isWatchlist, t.updatedAt, 'tv' AS mediaType " +
            "FROM tv_show t " +
            "INNER JOIN media_recommendation r " +
            "ON r.recommendedId = t.id " +
            "WHERE r.mediaType = 'tv' AND r.id = :tvShowId")
    fun getRecommendedTvShows(tvShowId: Int): Flow<List<ListItem>>

    @Query("SELECT * FROM media_genre WHERE id = :movieId AND mediaType = 'movie'")
    fun getMovieGenres(movieId: Int): Flow<List<MediaGenre>>

    @Query("SELECT * FROM media_genre WHERE id = :tvShowId AND mediaType = 'tv'")
    fun getTvShowGenres(tvShowId: Int): Flow<List<MediaGenre>>

    @Query("SELECT * FROM media_video WHERE id = :movieId AND mediaType = 'movie'")
    fun getMovieVideo(movieId: Int): Flow<MediaVideo>

    @Query("SELECT * FROM media_video WHERE id = :tvShowId AND mediaType = 'tv'")
    fun getTvShowVideo(tvShowId: Int): Flow<MediaVideo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrending(trendingMovies: List<Trending>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvShow(tvShow: TvShow)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendations(recommendations: List<MediaRecommendation>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvShows(tvShowShows: List<TvShow>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersons(persons: List<Person>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: Person)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchResults(searchResults: List<SearchResult>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCredits(credits: List<Credits>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaGenres(mediaGenres: List<MediaGenre>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaVideo(mediaVideo: MediaVideo)

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

    @Query("DELETE FROM trending")
    suspend fun deleteTrending()
}
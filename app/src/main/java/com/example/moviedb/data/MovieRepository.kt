package com.example.moviedb.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.example.moviedb.api.MovieApi
import com.example.moviedb.util.Resource
import com.example.moviedb.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieApi: MovieApi,
    private val movieDb: MovieDatabase
) {
    private val movieDao = movieDb.movieDao()

    fun getTop20Movies(
        forceRefresh: Boolean,
        onFetchSuccess: () -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ): Flow<Resource<List<ListItem>>> =
        networkBoundResource(
            query = {
                movieDao.getTop20Movies()
            },
            fetch = {
                val response = movieApi.getTopMovies(page = 1)
                response.results
            },
            saveFetchResult = { serverMovies ->
                val watchList = movieDao.getAllWatchlistMovies().first()

                val movies =
                    serverMovies.map { serverMovie ->
                        val isWatchlist = watchList.any { watchListMovie ->
                            watchListMovie.id == serverMovie.id
                        }
                        Movie(
                            id = serverMovie.id,
                            title = serverMovie.title,
                            releaseDate = serverMovie.release_date,
                            popularity = serverMovie.popularity,
                            voteAverage = serverMovie.vote_average,
                            voteCount = serverMovie.vote_count,
                            overview = serverMovie.overview,
                            backdropPath = serverMovie.backdrop_path,
                            posterPath = serverMovie.poster_path,
                            homepage = serverMovie.homepage,
                            runtime = serverMovie.runtime,
                            isWatchlist = isWatchlist
                        )
                    }
                movieDb.withTransaction {
                    movieDao.insertMovies(movies)
                }
            },
            shouldFetch = { cachedMovies ->
                if (forceRefresh) {
                    true
                } else {
                    val sortedMovies = cachedMovies.sortedBy { movie ->
                        movie.updatedAt
                    }
                    val oldestTimestamp = sortedMovies.firstOrNull()?.updatedAt
                    val needsRefresh = oldestTimestamp == null ||
                            oldestTimestamp < System.currentTimeMillis() -
                            TimeUnit.DAYS.toMillis(1)
                    needsRefresh
                }
            },
            onFetchSuccess = onFetchSuccess,
            onFetchFailed = { t ->
                if (t !is HttpException && t !is IOException) {
                    throw t
                }
                onFetchFailed(t)
            }
        )

    fun getPopular20Movies(
        forceRefresh: Boolean,
        onFetchSuccess: () -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ): Flow<Resource<List<ListItem>>> =
        networkBoundResource(
            query = {
                movieDao.getPopular20Movies()
            },
            fetch = {
                val response = movieApi.getPopularMovies(page = 1)
                response.results
            },
            saveFetchResult = { serverMovies ->
                val watchList = movieDao.getAllWatchlistMovies().first()

                val movies =
                    serverMovies.map { serverMovie ->
                        val isWatchlist = watchList.any { watchListMovie ->
                            watchListMovie.id == serverMovie.id
                        }
                        Movie(
                            id = serverMovie.id,
                            title = serverMovie.title,
                            releaseDate = serverMovie.release_date,
                            popularity = serverMovie.popularity,
                            voteAverage = serverMovie.vote_average,
                            voteCount = serverMovie.vote_count,
                            overview = serverMovie.overview,
                            backdropPath = serverMovie.backdrop_path,
                            posterPath = serverMovie.poster_path,
                            homepage = serverMovie.homepage,
                            runtime = serverMovie.runtime,
                            isWatchlist = isWatchlist
                        )
                    }
                movieDb.withTransaction {
                    movieDao.insertMovies(movies)
                }
            },
            shouldFetch = { cachedMovies ->
                if (forceRefresh) {
                    true
                } else {
                    val sortedMovies = cachedMovies.sortedBy { movie ->
                        movie.updatedAt
                    }
                    val oldestTimestamp = sortedMovies.firstOrNull()?.updatedAt
                    val needsRefresh = oldestTimestamp == null ||
                            oldestTimestamp < System.currentTimeMillis() -
                            TimeUnit.DAYS.toMillis(1)
                    needsRefresh
                }
            },
            onFetchSuccess = onFetchSuccess,
            onFetchFailed = { t ->
                if (t !is HttpException && t !is IOException) {
                    throw t
                }
                onFetchFailed(t)
            }
        )

    fun getTop20TvShows(
        forceRefresh: Boolean,
        onFetchSuccess: () -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ): Flow<Resource<List<ListItem>>> =
        networkBoundResource(
            query = {
                movieDao.getTop20TvShows()
            },
            fetch = {
                val response = movieApi.getTopTvShows(page = 1)
                response.results
            },
            saveFetchResult = { serverTvShows ->
                val watchList = movieDao.getAllWatchlistTvShows().first()

                val tvShows =
                    serverTvShows.map { serverTvShow ->
                        val isWatchlist = watchList.any { watchListTvShow ->
                            watchListTvShow.id == serverTvShow.id
                        }
                        TvShow(
                            id = serverTvShow.id,
                            title = serverTvShow.name,
                            releaseDate = serverTvShow.first_air_date,
                            lastAirDate = serverTvShow.last_air_date,
                            status = serverTvShow.status,
                            popularity = serverTvShow.popularity,
                            voteAverage = serverTvShow.vote_average,
                            voteCount = serverTvShow.vote_count,
                            overview = serverTvShow.overview,
                            backdropPath = serverTvShow.backdrop_path,
                            posterPath = serverTvShow.poster_path,
                            homepage = serverTvShow.homepage,
                            isWatchlist = isWatchlist
                        )
                    }
                movieDb.withTransaction {
                    movieDao.insertTvShows(tvShows)
                }
            },
            shouldFetch = { cachedTvShows ->
                if (forceRefresh) {
                    true
                } else {
                    val sortedTvShows = cachedTvShows.sortedBy { tvShow ->
                        tvShow.updatedAt
                    }
                    val oldestTimestamp = sortedTvShows.firstOrNull()?.updatedAt
                    val needsRefresh = oldestTimestamp == null ||
                            oldestTimestamp < System.currentTimeMillis() -
                            TimeUnit.DAYS.toMillis(1)
                    needsRefresh
                }
            },
            onFetchSuccess = onFetchSuccess,
            onFetchFailed = { t ->
                if (t !is HttpException && t !is IOException) {
                    throw t
                }
                onFetchFailed(t)
            }
        )

    fun getPopular20TvShows(
        forceRefresh: Boolean,
        onFetchSuccess: () -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ): Flow<Resource<List<ListItem>>> =
        networkBoundResource(
            query = {
                movieDao.getPopular20TvShows()
            },
            fetch = {
                val response = movieApi.getPopularTvShows(page = 1)
                response.results
            },
            saveFetchResult = { serverTvShows ->
                val watchList = movieDao.getAllWatchlistTvShows().first()

                val tvShows =
                    serverTvShows.map { serverTvShow ->
                        val isWatchlist = watchList.any { watchListTvShow ->
                            watchListTvShow.id == serverTvShow.id
                        }
                        TvShow(
                            id = serverTvShow.id,
                            title = serverTvShow.name,
                            releaseDate = serverTvShow.first_air_date,
                            lastAirDate = serverTvShow.last_air_date,
                            status = serverTvShow.status,
                            popularity = serverTvShow.popularity,
                            voteAverage = serverTvShow.vote_average,
                            voteCount = serverTvShow.vote_count,
                            overview = serverTvShow.overview,
                            backdropPath = serverTvShow.backdrop_path,
                            posterPath = serverTvShow.poster_path,
                            homepage = serverTvShow.homepage,
                            isWatchlist = isWatchlist
                        )
                    }
                movieDb.withTransaction {
                    movieDao.insertTvShows(tvShows)
                }
            },
            shouldFetch = { cachedTvShows ->
                if (forceRefresh) {
                    true
                } else {
                    val sortedTvShows = cachedTvShows.sortedBy { tvShow ->
                        tvShow.updatedAt
                    }
                    val oldestTimestamp = sortedTvShows.firstOrNull()?.updatedAt
                    val needsRefresh = oldestTimestamp == null ||
                            oldestTimestamp < System.currentTimeMillis() -
                            TimeUnit.DAYS.toMillis(1)
                    needsRefresh
                }
            },
            onFetchSuccess = onFetchSuccess,
            onFetchFailed = { t ->
                if (t !is HttpException && t !is IOException) {
                    throw t
                }
                onFetchFailed(t)
            }
        )

    fun getSearchResultsPaged(query: String): Flow<PagingData<SearchListItem>> =
        Pager(
            config = PagingConfig(pageSize = 20, maxSize = 200),
            remoteMediator = SearchMediaRemoteMediator(query, movieApi, movieDb),
            pagingSourceFactory = { movieDao.getSearchResultMediaPaged(query) }
        ).flow

    fun getAllWatchlistMedia(): Flow<List<ListItem>> =
        movieDao.getAllWatchlistMedia()

    suspend fun updateMovie(movie: Movie) {
        movieDao.updateMovie(movie)
    }

    suspend fun updateTvShow(tvShow: TvShow) {
        movieDao.updateTvShow(tvShow)
    }

    suspend fun getMovie(id: Int): Movie {
        return movieDao.getMovie(id)
    }

    suspend fun getTvShow(id: Int): TvShow {
        return movieDao.getTvShow(id)
    }

    suspend fun resetWatchlist() {
        movieDao.resetAllMovieWatchlist()
        movieDao.resetAllTvShowWatchlist()
    }

    suspend fun deleteNonWatchlistMoviesAndTvShowsOlderThan(timestampInMillis: Long) {
        movieDao.deleteNonWatchlistMoviesOlderThan(timestampInMillis)
        movieDao.deleteNonWatchlistTvShowsOlderThan(timestampInMillis)
    }
}
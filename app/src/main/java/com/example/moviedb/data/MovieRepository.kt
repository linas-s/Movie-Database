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
                val response =
                    movieApi.getTopMovies(page = 1).results + movieApi.getTopMovies(page = 2).results
                response
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
                            status = null,
                            budget = null,
                            tagline = serverMovie.tagline,
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
                            status = null,
                            budget = null,
                            tagline = serverMovie.tagline,
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
                val response =
                    movieApi.getTopTvShows(page = 1).results + movieApi.getTopTvShows(page = 2).results
                response
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

    fun getMovieFlow(
        movie: Movie
    ): Flow<Resource<Movie>> =
        networkBoundResource(
            query = {
                movieDao.getMovieFlow(movie.id)
            },
            fetch = {
                val movieDetails = movieApi.getMovieDetails(id = movie.id)
                movieDetails
            },
            saveFetchResult = { serverMovieDetails ->
                val watchList = movieDao.getAllWatchlistMovies().first()

                val isWatchlist = watchList.any { watchListMovie ->
                    watchListMovie.id == serverMovieDetails.id
                }

                val movie =
                    Movie(
                        id = serverMovieDetails.id,
                        title = serverMovieDetails.title,
                        releaseDate = serverMovieDetails.release_date,
                        popularity = serverMovieDetails.popularity,
                        voteAverage = serverMovieDetails.vote_average,
                        voteCount = serverMovieDetails.vote_count,
                        overview = serverMovieDetails.overview,
                        status = serverMovieDetails.status,
                        budget = serverMovieDetails.budget,
                        tagline = serverMovieDetails.tagline,
                        backdropPath = serverMovieDetails.backdrop_path,
                        posterPath = serverMovieDetails.poster_path,
                        homepage = serverMovieDetails.homepage,
                        runtime = serverMovieDetails.runtime,
                        isWatchlist = isWatchlist
                    )

                val genres = serverMovieDetails.genres.map { genre ->
                    MediaGenre(
                        mediaType = "movie",
                        id = serverMovieDetails.id,
                        genreId = genre.id,
                        genreName = genre.name
                    )
                }
                movieDb.withTransaction {
                    movieDao.insertMovie(movie)
                    movieDao.insertMediaGenres(genres)
                }
            }
        )

    fun getMovieRecommendations(
        movie: Movie
    ): Flow<Resource<List<ListItem>>> =
        networkBoundResource(
            query = {
                movieDao.getRecommendedMovies(movie.id)
            },
            fetch = {
                val response = movieApi.getRecommendedMovies(id = movie.id, page = 1)
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
                            status = null,
                            budget = null,
                            tagline = serverMovie.tagline,
                            homepage = serverMovie.homepage,
                            runtime = serverMovie.runtime,
                            isWatchlist = isWatchlist
                        )
                    }
                val movieRecommendations =
                    serverMovies.map { serverMovie ->
                        MediaRecommendation(
                            mediaType = "movie",
                            id = movie.id,
                            recommendedId = serverMovie.id
                        )
                    }
                movieDb.withTransaction {
                    movieDao.insertMovies(movies)
                    movieDao.insertRecommendations(movieRecommendations)
                }
            }
        )

    fun getMovieCast(
        movie: Movie
    ): Flow<Resource<List<CastCrewPerson>>> =
        networkBoundResource(
            query = {
                movieDao.getMovieCast(movie.id)
            },
            fetch = {
                val response = movieApi.getMovieCredits(movie.id)
                response.cast
            },
            saveFetchResult = { serverCast ->
                val cast =
                    serverCast.map { serverCast ->
                        Person(
                            id = serverCast.id,
                            title = serverCast.name,
                            posterPath = serverCast.profile_path,
                            popularity = serverCast.popularity,
                            knownForDepartment = serverCast.known_for_department,
                            birthday = null,
                            deathDay = null,
                            homepage = null,
                        )
                    }

                val credits =
                    serverCast.map { serverCast ->
                        Credits(
                            mediaType = "movie",
                            mediaId = movie.id,
                            personId = serverCast.id,
                            character = serverCast.character,
                            job = "acting",
                            listOrder = serverCast.order
                        )
                    }
                movieDb.withTransaction {
                    movieDao.insertPersons(cast)
                    movieDao.insertCredits(credits)
                }
            }
        )

    fun getMovieCrew(
        movie: Movie
    ): Flow<Resource<List<CastCrewPerson>>> =
        networkBoundResource(
            query = {
                movieDao.getMovieCrew(movie.id)
            },
            fetch = {
                val response = movieApi.getMovieCredits(movie.id)
                response.crew
            },
            saveFetchResult = { serverCrew ->
                val crew =
                    serverCrew.map { serverCrew ->
                        Person(
                            id = serverCrew.id,
                            title = serverCrew.name,
                            posterPath = serverCrew.profile_path,
                            popularity = serverCrew.popularity,
                            knownForDepartment = serverCrew.known_for_department,
                            birthday = null,
                            deathDay = null,
                            homepage = null,
                        )
                    }

                val credits =
                    serverCrew.map { serverCrew ->
                        Credits(
                            mediaType = "movie",
                            mediaId = movie.id,
                            personId = serverCrew.id,
                            character = null,
                            job = serverCrew.job,
                            listOrder = serverCrew.order
                        )
                    }
                movieDb.withTransaction {
                    movieDao.insertPersons(crew)
                    movieDao.insertCredits(credits)
                }
            }
        )

    fun getMovieGenres(
        movie: Movie
    ): Flow<Resource<List<MediaGenre>>> =
        networkBoundResource(
            query = {
                movieDao.getMovieGenres(movie.id)
            },
            fetch = { },
            saveFetchResult = { }
        )

    fun getMovieVideo(
        movie: Movie
    ): Flow<Resource<MediaVideo>> =
        networkBoundResource(
            query = {
                movieDao.getMovieVideo(movie.id)
            },
            fetch = {
                val response = movieApi.getMovieVideos(movie.id)
                val filtered = response.results.filter { serverMovies ->
                    serverMovies.site == "YouTube" &&
                    serverMovies.type == "Trailer"
                }
                filtered
            },
            saveFetchResult = { serverVideo ->
                val videos = serverVideo.map { serverVideo ->
                        MediaVideo(
                            mediaType = "movie",
                            id = movie.id,
                            key = serverVideo.key,
                            publishedAt = serverVideo.published_at
                        )
                    }
                val latestMovieVideo = videos.sortedBy {
                    it.publishedAt
                }.last()
                movieDb.withTransaction {
                    movieDao.insertMediaVideo(latestMovieVideo)
                }
            }
        )

    fun getSearchResultsPaged(
        query: String,
        refreshOnInit: Boolean
    ): Flow<PagingData<SearchListItem>> =
        Pager(
            config = PagingConfig(pageSize = 20, maxSize = 200),
            remoteMediator = SearchMediaRemoteMediator(query, movieApi, movieDb, refreshOnInit),
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
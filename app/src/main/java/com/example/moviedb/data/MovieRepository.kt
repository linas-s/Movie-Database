package com.example.moviedb.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.PrimaryKey
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
                            isWatchlist = isWatchlist,
                            numberOfSeasons = null,
                            tagline = ""
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
                            isWatchlist = isWatchlist,
                            numberOfSeasons = null,
                            tagline = ""
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
        listItem: ListItem
    ): Flow<Resource<MediaDetails>> =
        networkBoundResource(
            query = {
                movieDao.getMovieFlow(listItem.id)
            },
            fetch = {
                val movieDetails = movieApi.getMovieDetails(id = listItem.id)
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

    fun getTvShowFlow(
        listItem: ListItem
    ): Flow<Resource<MediaDetails>> =
        networkBoundResource(
            query = {
                movieDao.getTvShowFlow(listItem.id)
            },
            fetch = {
                val tvShowDetails = movieApi.getTvShowDetails(id = listItem.id)
                tvShowDetails
            },
            saveFetchResult = { serverTvShowDetails ->
                val watchList = movieDao.getAllWatchlistTvShows().first()

                val isWatchlist = watchList.any { watchListTvShow ->
                    watchListTvShow.id == serverTvShowDetails.id
                }

                val tvShow =
                    TvShow(
                        id = serverTvShowDetails.id,
                        title = serverTvShowDetails.name,
                        releaseDate = serverTvShowDetails.first_air_date,
                        popularity = serverTvShowDetails.popularity,
                        voteAverage = serverTvShowDetails.vote_average,
                        voteCount = serverTvShowDetails.vote_count,
                        overview = serverTvShowDetails.overview,
                        status = serverTvShowDetails.status,
                        tagline = serverTvShowDetails.tagline,
                        backdropPath = serverTvShowDetails.backdrop_path,
                        posterPath = serverTvShowDetails.poster_path,
                        homepage = serverTvShowDetails.homepage,
                        isWatchlist = isWatchlist,
                        lastAirDate = serverTvShowDetails.last_air_date,
                        numberOfSeasons = serverTvShowDetails.number_of_seasons
                    )

                val genres = serverTvShowDetails.genres.map { genre ->
                    MediaGenre(
                        mediaType = "tv",
                        id = serverTvShowDetails.id,
                        genreId = genre.id,
                        genreName = genre.name
                    )
                }

                val creators = serverTvShowDetails.created_by.map { creator ->
                    Person(
                        id = creator.id,
                        title = creator.name,
                        posterPath = creator.profile_path,
                        popularity = 0.0,
                        knownForDepartment = "Producing",
                        birthday = null,
                        deathDay = null,
                        homepage = null,
                        placeOfBirth = null,
                        biography = null
                    )
                }

                val credits = serverTvShowDetails.created_by.map { creator ->
                    Credits(
                        mediaType = listItem.mediaType,
                        mediaId = listItem.id,
                        personId = creator.id,
                        character = null,
                        job = "Creator",
                        listOrder = -1
                    )
                }

                movieDb.withTransaction {
                    movieDao.insertTvShow(tvShow)
                    movieDao.insertMediaGenres(genres)
                    movieDao.insertPersons(creators)
                    movieDao.insertCredits(credits)
                }
            }
        )

    fun getMovieRecommendations(
        listItem: ListItem
    ): Flow<Resource<List<ListItem>>> =
        networkBoundResource(
            query = {
                movieDao.getRecommendedMovies(listItem.id)
            },
            fetch = {
                val response = movieApi.getRecommendedMovies(id = listItem.id, page = 1)
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
                            id = listItem.id,
                            recommendedId = serverMovie.id
                        )
                    }
                movieDb.withTransaction {
                    movieDao.insertMovies(movies)
                    movieDao.insertRecommendations(movieRecommendations)
                }
            }
        )

    fun getTvShowRecommendations(
        listItem: ListItem
    ): Flow<Resource<List<ListItem>>> =
        networkBoundResource(
            query = {
                movieDao.getRecommendedTvShows(listItem.id)
            },
            fetch = {
                val response = movieApi.getRecommendedTvShows(id = listItem.id, page = 1)
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
                            popularity = serverTvShow.popularity,
                            voteAverage = serverTvShow.vote_average,
                            voteCount = serverTvShow.vote_count,
                            overview = serverTvShow.overview,
                            backdropPath = serverTvShow.backdrop_path,
                            posterPath = serverTvShow.poster_path,
                            status = null,
                            tagline = serverTvShow.tagline,
                            homepage = serverTvShow.homepage,
                            isWatchlist = isWatchlist,
                            lastAirDate = serverTvShow.last_air_date,
                            numberOfSeasons = serverTvShow.numberOfSeasons
                        )
                    }
                val tvShowRecommendations =
                    serverTvShows.map { serverTvShow ->
                        MediaRecommendation(
                            mediaType = "tv",
                            id = listItem.id,
                            recommendedId = serverTvShow.id
                        )
                    }
                movieDb.withTransaction {
                    movieDao.insertTvShows(tvShows)
                    movieDao.insertRecommendations(tvShowRecommendations)
                }
            }
        )

    fun getMediaCast(
        listItem: ListItem
    ): Flow<Resource<List<CastCrewPerson>>> =
        networkBoundResource(
            query = {
                val mediaCast = if (listItem.mediaType == "movie") {
                    movieDao.getMovieCast(listItem.id)
                } else {
                    movieDao.getTvShowCast(listItem.id)
                }
                mediaCast
            },
            fetch = {
                val response = if (listItem.mediaType == "movie") {
                    movieApi.getMovieCredits(listItem.id)
                } else {
                    movieApi.getTvShowCredits(listItem.id)
                }
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
                            placeOfBirth = null,
                            biography = null
                        )
                    }

                val credits =
                    serverCast.map { serverCast ->
                        Credits(
                            mediaType = listItem.mediaType,
                            mediaId = listItem.id,
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

    fun getMediaCrew(
        listItem: ListItem
    ): Flow<Resource<List<CastCrewPerson>>> =
        networkBoundResource(
            query = {
                val mediaCrew = if (listItem.mediaType == "movie") {
                    movieDao.getMovieCrew(listItem.id)
                } else {
                    movieDao.getTvShowCrew(listItem.id)
                }
                mediaCrew
            },
            fetch = {
                val response = if (listItem.mediaType == "movie") {
                    movieApi.getMovieCredits(listItem.id)
                } else {
                    movieApi.getTvShowCredits(listItem.id)
                }
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
                            placeOfBirth = null,
                            biography = null
                        )
                    }

                val credits =
                    serverCrew.map { serverCrew ->
                        Credits(
                            mediaType = listItem.mediaType,
                            mediaId = listItem.id,
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

    fun getMediaGenres(
        listItem: ListItem
    ): Flow<Resource<List<MediaGenre>>> =
        networkBoundResource(
            query = {
                val genres = if (listItem.mediaType == "movie") {
                    movieDao.getMovieGenres(listItem.id)
                } else {
                    movieDao.getTvShowGenres(listItem.id)
                }
                genres
            },
            fetch = { },
            saveFetchResult = { }
        )

    fun getMediaVideo(
        listItem: ListItem
    ): Flow<Resource<MediaVideo>> =
        networkBoundResource(
            query = {
                val mediaVideo = if (listItem.mediaType == "movie") {
                    movieDao.getMovieVideo(listItem.id)
                } else {
                    movieDao.getTvShowVideo(listItem.id)
                }
                mediaVideo
            },
            fetch = {
                val response = if (listItem.mediaType == "movie") {
                    movieApi.getMovieVideos(listItem.id)
                } else {
                    movieApi.getTvShowVideos(listItem.id)
                }
                val filtered = response.results.filter { serverMovies ->
                    serverMovies.site == "YouTube" &&
                            serverMovies.type == "Trailer"
                }
                filtered
            },
            saveFetchResult = { serverVideo ->
                val videos = serverVideo.map { serverVideo ->
                    MediaVideo(
                        mediaType = listItem.mediaType,
                        id = listItem.id,
                        key = serverVideo.key,
                        publishedAt = serverVideo.published_at
                    )
                }
                val latestMediaVideo = videos.sortedBy {
                    it.publishedAt
                }.last()
                movieDb.withTransaction {
                    movieDao.insertMediaVideo(latestMediaVideo)
                }
            }
        )

    fun getPersonDetails(
        personId: Int
    ): Flow<Resource<Person>> =
        networkBoundResource(
            query = {
                val person = movieDao.getPerson(personId)
                person
            },
            fetch = {
                val response = movieApi.getPersonDetails(personId)
                response
            },
            saveFetchResult = { serverPerson ->
                val person = Person(
                    id = serverPerson.id,
                    title = serverPerson.name,
                    posterPath = serverPerson.profile_path,
                    popularity = serverPerson.popularity,
                    knownForDepartment = serverPerson.known_for_department,
                    birthday = serverPerson.birthday,
                    deathDay = serverPerson.deathday,
                    homepage = serverPerson.homepage,
                    placeOfBirth = serverPerson.place_of_birth,
                    biography = serverPerson.biography
                )
                movieDb.withTransaction {
                    movieDao.insertPerson(person)
                }
            }
        )

    fun getPersonMediaCast(
        personId: Int
    ): Flow<Resource<List<ListItem>>> =
        networkBoundResource(
            query = {
                val personMoviesCast = movieDao.getPersonMediaCast(personId)
                personMoviesCast
            },
            fetch = {
                val response = movieApi.getPersonMedia(personId)
                response.cast
            },
            saveFetchResult = { serverMedia ->

                val movieWatchList = movieDao.getAllWatchlistMovies().first()

                val movies = serverMedia.filter { serverMedia ->
                    serverMedia.media_type == "movie"
                }.map { serverMedia ->
                    val isWatchlist = movieWatchList.any { watchListMovie ->
                        watchListMovie.id == serverMedia.id
                    }
                    Movie(
                        id = serverMedia.id,
                        title = serverMedia.title ?: "",
                        releaseDate = serverMedia.release_date ?: "",
                        popularity = serverMedia.popularity,
                        voteAverage = serverMedia.vote_average,
                        voteCount = serverMedia.vote_count,
                        overview = serverMedia.overview,
                        status = null,
                        budget = null,
                        tagline = null,
                        backdropPath = serverMedia.backdrop_path,
                        posterPath = serverMedia.poster_path,
                        homepage = null,
                        runtime = 0,
                        isWatchlist = isWatchlist
                    )
                }

                val tvShowWatchList = movieDao.getAllWatchlistTvShows().first()

                val tvShows = serverMedia.filter { serverMedia ->
                    serverMedia.media_type == "tv"
                }.map { serverMedia ->
                    val isWatchlist = tvShowWatchList.any { watchListTvShow ->
                        watchListTvShow.id == serverMedia.id
                    }
                    TvShow(
                        id = serverMedia.id,
                        title = serverMedia.name ?: "",
                        releaseDate = serverMedia.first_air_date ?: "",
                        popularity = serverMedia.popularity,
                        voteAverage = serverMedia.vote_average,
                        voteCount = serverMedia.vote_count,
                        overview = serverMedia.overview,
                        lastAirDate = null,
                        numberOfSeasons = null,
                        status = null,
                        tagline = null,
                        backdropPath = serverMedia.backdrop_path,
                        posterPath = serverMedia.poster_path,
                        homepage = null,
                        isWatchlist = isWatchlist
                    )
                }

                val credits =
                    serverMedia.map { serverCast ->
                        Credits(
                            mediaType = serverCast.media_type,
                            mediaId = serverCast.id,
                            personId = personId,
                            character = serverCast.character,
                            job = "acting",
                            listOrder = 0
                        )
                    }
                movieDb.withTransaction {
                    movieDao.insertMovies(movies)
                    movieDao.insertTvShows(tvShows)
                    movieDao.insertCredits(credits)
                }
            }
        )

    fun getPersonMediaCrew(
        personId: Int
    ): Flow<Resource<List<ListItem>>> =
        networkBoundResource(
            query = {
                val personMoviesCrew = movieDao.getPersonMediaCrew(personId)
                personMoviesCrew
            },
            fetch = {
                val response = movieApi.getPersonMedia(personId)
                response.crew
            },
            saveFetchResult = { serverMedia ->

                val movieWatchList = movieDao.getAllWatchlistMovies().first()

                val movies = serverMedia.filter { serverMedia ->
                    serverMedia.media_type == "movie"
                }.map { serverMedia ->
                    val isWatchlist = movieWatchList.any { watchListMovie ->
                        watchListMovie.id == serverMedia.id
                    }
                    Movie(
                        id = serverMedia.id,
                        title = serverMedia.title ?: "",
                        releaseDate = serverMedia.release_date ?: "",
                        popularity = serverMedia.popularity,
                        voteAverage = serverMedia.vote_average,
                        voteCount = serverMedia.vote_count,
                        overview = serverMedia.overview,
                        status = null,
                        budget = null,
                        tagline = null,
                        backdropPath = serverMedia.backdrop_path,
                        posterPath = serverMedia.poster_path,
                        homepage = null,
                        runtime = 0,
                        isWatchlist = isWatchlist
                    )
                }

                val tvShowWatchList = movieDao.getAllWatchlistTvShows().first()

                val tvShows = serverMedia.filter { serverMedia ->
                    serverMedia.media_type == "tv"
                }.map { serverMedia ->
                    val isWatchlist = tvShowWatchList.any { watchListTvShow ->
                        watchListTvShow.id == serverMedia.id
                    }
                    TvShow(
                        id = serverMedia.id,
                        title = serverMedia.name ?: "",
                        releaseDate = serverMedia.first_air_date ?: "",
                        popularity = serverMedia.popularity,
                        voteAverage = serverMedia.vote_average,
                        voteCount = serverMedia.vote_count,
                        overview = serverMedia.overview,
                        lastAirDate = null,
                        numberOfSeasons = null,
                        status = null,
                        tagline = null,
                        backdropPath = serverMedia.backdrop_path,
                        posterPath = serverMedia.poster_path,
                        homepage = null,
                        isWatchlist = isWatchlist
                    )
                }

                val credits =
                    serverMedia.map { serverCrew ->
                        Credits(
                            mediaType = serverCrew.media_type,
                            mediaId = serverCrew.id,
                            personId = personId,
                            character = null,
                            job = "crew",
                            listOrder = 0
                        )
                    }
                movieDb.withTransaction {
                    movieDao.insertMovies(movies)
                    movieDao.insertTvShows(tvShows)
                    movieDao.insertCredits(credits)
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
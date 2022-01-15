package com.example.moviedb.data

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.PrimaryKey
import androidx.room.withTransaction
import com.example.moviedb.api.MovieApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException

private const val MEDIA_STARTING_PAGE_INDEX = 1

class SearchMediaRemoteMediator(
    private val searchQuery: String,
    private val movieApi: MovieApi,
    private val movieDb: MovieDatabase
) : RemoteMediator<Int, SearchListItem>() {

    private val movieDao = movieDb.movieDao()
    private val searchQueryRemoteKeyDao = movieDb.searchQueryRemoteKeyDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SearchListItem>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> MEDIA_STARTING_PAGE_INDEX
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> searchQueryRemoteKeyDao.getRemoteKey(searchQuery).nextPageKey
        }

        try {
            val response = movieApi.searchMedia(query = searchQuery, page = page)

            val serverSearchResults = response.results

            val watchList = movieDao.getAllWatchlistMedia().first()

            val searchResultMovies = serverSearchResults.filter { serverSearchResultMedia ->
                serverSearchResultMedia.media_type == "movie"
            }.map { serverSearchResultMedia ->
                val isWatchlist = watchList.any { watchListMedia ->
                    watchListMedia.mediaType == serverSearchResultMedia.media_type &&
                            watchListMedia.id == serverSearchResultMedia.id
                }
                Movie(
                    id = serverSearchResultMedia.id,
                    title = serverSearchResultMedia.title ?: "",
                    releaseDate = serverSearchResultMedia.release_date ?: "",
                    popularity = serverSearchResultMedia.popularity,
                    voteAverage = serverSearchResultMedia.vote_average ?: 0.0,
                    voteCount = serverSearchResultMedia.vote_count ?: 0,
                    overview = serverSearchResultMedia.overview,
                    backdropPath = serverSearchResultMedia.backdrop_path,
                    posterPath = serverSearchResultMedia.poster_path,
                    homepage = null,
                    runtime = null,
                    isWatchlist = isWatchlist
                )
            }

            val searchResultTvShows = serverSearchResults.filter { serverSearchResultMedia ->
                serverSearchResultMedia.media_type == "tv"
            }.map { serverSearchResultMedia ->
                val isWatchlist = watchList.any { watchListMedia ->
                    watchListMedia.mediaType == serverSearchResultMedia.media_type &&
                            watchListMedia.id == serverSearchResultMedia.id
                }
                TvShow(
                    id = serverSearchResultMedia.id,
                    title = serverSearchResultMedia.name ?: "",
                    releaseDate = serverSearchResultMedia.first_air_date ?: "",
                    lastAirDate = null,
                    status = null,
                    popularity = serverSearchResultMedia.popularity,
                    voteAverage = serverSearchResultMedia.vote_average ?: 0.0,
                    voteCount = serverSearchResultMedia.vote_count ?: 0,
                    overview = serverSearchResultMedia.overview ?: "",
                    backdropPath = serverSearchResultMedia.backdrop_path,
                    posterPath = serverSearchResultMedia.poster_path,
                    homepage = null,
                    isWatchlist = isWatchlist
                )
            }

            val searchResultPersons = serverSearchResults.filter { serverSearchResultMedia ->
                serverSearchResultMedia.media_type == "person"
            }.map { serverSearchResultMedia ->
                Person(
                    id = serverSearchResultMedia.id,
                    title = serverSearchResultMedia.name ?: "",
                    posterPath = serverSearchResultMedia.profile_path,
                    popularity = serverSearchResultMedia.popularity,
                    knownForDepartment = serverSearchResultMedia.known_for_department ?: "",
                    birthday = null,
                    deathDay = null,
                    homepage = null
                )
            }

            movieDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    movieDao.deleteSearchResultsForQuery(searchQuery)
                }

                val lastQueryPosition = movieDao.getLastQueryPosition(searchQuery) ?: 0
                var queryPosition = lastQueryPosition + 1

                val searchResults = serverSearchResults.map { media ->
                    SearchResult(searchQuery, media.media_type, media.id, queryPosition++)
                }

                val nextPageKey = page + 1

                movieDao.insertMovies(searchResultMovies)
                movieDao.insertTvShows(searchResultTvShows)
                movieDao.insertPersons(searchResultPersons)

                movieDao.insertSearchResults(searchResults)
                searchQueryRemoteKeyDao.insertRemoteKey(
                    SearchQueryRemoteKey(searchQuery, nextPageKey)
                )
            }
            return MediatorResult.Success(endOfPaginationReached = serverSearchResults.isEmpty())
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }
}
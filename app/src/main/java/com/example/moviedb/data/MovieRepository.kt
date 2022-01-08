package com.example.moviedb.data

import androidx.room.PrimaryKey
import androidx.room.withTransaction
import com.example.moviedb.api.MovieApi
import com.example.moviedb.util.Resource
import com.example.moviedb.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieApi: MovieApi,
    private val movieDb: MovieDatabase
) {
    private val movieDao = movieDb.movieDao()

    fun getTopMovies(
        onFetchSuccess: () -> Unit,
        onFetchFailed: (Throwable) -> Unit
    ): Flow<Resource<List<Movie>>> =
        networkBoundResource(
            query = {
                movieDao.getTop20Movies()
            },
            fetch = {
                val response = movieApi.getTopMovies(page = 1)
                response.results
            },
            saveFetchResult = { serverMovies ->
                val movies =
                    serverMovies.map { serverMovie ->
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
                            isWatchlist = false
                        )
                    }

                movieDb.withTransaction {
                    movieDao.insertMovies(movies)
                }
            },
            onFetchSuccess = onFetchSuccess,
            onFetchFailed = { t->
                if (t !is HttpException && t !is IOException){
                    throw t
                }
                onFetchFailed(t)
            }
        )
}
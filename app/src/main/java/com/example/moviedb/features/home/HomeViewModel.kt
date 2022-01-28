package com.example.moviedb.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviedb.data.ListItem
import com.example.moviedb.data.Movie
import com.example.moviedb.data.MovieRepository
import com.example.moviedb.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val refreshTriggerChannel = Channel<Refresh>()
    private val refreshTrigger =
        refreshTriggerChannel.receiveAsFlow().shareIn(viewModelScope, SharingStarted.Lazily, 1)

    var top20MoviesPendingScrollToTopAfterRefresh = false
    var popular20MoviesPendingScrollToTopAfterRefresh = false
    var top20TvShowsPendingScrollToTopAfterRefresh = false
    var popular20TvShowsPendingScrollToTopAfterRefresh = false

    val top20Movies = refreshTrigger.flatMapLatest { refresh ->
        repository.getTop20Movies(
            refresh == Refresh.FORCE,
            onFetchSuccess = {
                top20MoviesPendingScrollToTopAfterRefresh = true
            },
            onFetchFailed = { t ->
                viewModelScope.launch { eventChannel.send(Event.ShowErrorMessage(t)) }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val popular20Movies = refreshTrigger.flatMapLatest { refresh ->
        repository.getPopular20Movies(
            refresh == Refresh.FORCE,
            onFetchSuccess = {
                popular20MoviesPendingScrollToTopAfterRefresh = true
            },
            onFetchFailed = { t ->
                viewModelScope.launch { eventChannel.send(Event.ShowErrorMessage(t)) }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val top20TvShows = refreshTrigger.flatMapLatest { refresh ->
        repository.getTop20TvShows(
            refresh == Refresh.FORCE,
            onFetchSuccess = {
                top20TvShowsPendingScrollToTopAfterRefresh = true
            },
            onFetchFailed = { t ->
                viewModelScope.launch { eventChannel.send(Event.ShowErrorMessage(t)) }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val popular20TvShows = refreshTrigger.flatMapLatest { refresh ->
        repository.getPopular20TvShows(
            refresh == Refresh.FORCE,
            onFetchSuccess = {
                popular20TvShowsPendingScrollToTopAfterRefresh = true
            },
            onFetchFailed = { t ->
                viewModelScope.launch { eventChannel.send(Event.ShowErrorMessage(t)) }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        viewModelScope.launch {
            repository.deleteNonWatchlistMoviesAndTvShowsOlderThan(
                System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
            )
        }
    }

    fun onStart() {
        if (top20Movies.value !is Resource.Loading ||
            popular20Movies.value !is Resource.Loading ||
            top20TvShows.value !is Resource.Loading ||
            popular20TvShows.value !is Resource.Loading
        ) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.NORMAL)
            }
        }
    }

    fun onManualRefresh() {
        if (top20Movies.value !is Resource.Loading ||
            popular20Movies.value !is Resource.Loading ||
            top20TvShows.value !is Resource.Loading ||
            popular20TvShows.value !is Resource.Loading
        ) {
            viewModelScope.launch {
                refreshTriggerChannel.send(Refresh.FORCE)
            }
        }
    }

    fun onWatchlistClick(listItem: ListItem) {
        viewModelScope.launch {
            when (listItem.mediaType) {
                "movie" -> {
                    val movie = repository.getMovie(listItem.id)
                    val currentlyWatchlist = movie.isWatchlist
                    val updatedMovie = movie.copy(isWatchlist = !currentlyWatchlist)
                    repository.updateMovie(updatedMovie)
                }
                else -> {
                    val tvShow = repository.getTvShow(listItem.id)
                    val currentlyWatchlist = tvShow.isWatchlist
                    val updatedTvShow = tvShow.copy(isWatchlist = !currentlyWatchlist)
                    repository.updateTvShow(updatedTvShow)
                }
            }
        }
    }

    fun onItemListClick(listItem: ListItem) {
        viewModelScope.launch {
            onMediaSelected(listItem)
        }
    }

    private fun onMediaSelected(listItem: ListItem) = viewModelScope.launch {
        eventChannel.send(Event.NavigateToMediaDetailsFragment(listItem))
    }

    fun onTrailerClick(listItem: ListItem){
        viewModelScope.launch {
            onTrailerSelected(listItem)
        }
    }

    private fun onTrailerSelected(listItem: ListItem) = viewModelScope.launch {
        repository.getMediaVideo(listItem).collectLatest { result ->
            if(result is Resource.Success) {
                eventChannel.send(Event.OpenMediaTrailer(result.data?.key))
                cancel()
            }
        }
    }

    enum class Refresh {
        FORCE, NORMAL
    }

    sealed class Event {
        data class ShowErrorMessage(val error: Throwable) : Event()
        data class NavigateToMediaDetailsFragment(val listItem: ListItem) : Event()
        data class OpenMediaTrailer(val key: String?) : Event()
    }

}
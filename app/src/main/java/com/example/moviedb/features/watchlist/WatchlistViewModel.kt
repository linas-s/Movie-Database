package com.example.moviedb.features.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviedb.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    val watchlist = repository.getAllWatchlistMedia()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

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

    fun onWatchlistItemClick(listItem: ListItem) {
        viewModelScope.launch {
            onMediaSelected(listItem)
        }
    }

    private fun onMediaSelected(listItem: ListItem) = viewModelScope.launch {
        eventChannel.send(Event.NavigateToMediaDetailsFragment(listItem))
    }

    sealed class Event {
        data class NavigateToMediaDetailsFragment(val listItem: ListItem) : Event()
    }

    fun onDeleteWatchlist() {
        viewModelScope.launch {
            repository.resetWatchlist()
        }
    }
}
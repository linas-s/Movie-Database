package com.example.moviedb.features.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviedb.data.*
import com.example.moviedb.features.search.SearchViewModel
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
            when (listItem.mediaType) {
                "movie" -> {
                    val movie = repository.getMovie(listItem.id)
                    onMovieSelected(movie)
                }
                "tv_show" -> {
                    val tvShow = repository.getTvShow(listItem.id)
                    onTvShowSelected(tvShow)
                }
            }
        }
    }

    private fun onMovieSelected(movie: Movie) = viewModelScope.launch {
        eventChannel.send(WatchlistViewModel.Event.NavigateToMovieDetailsFragment(movie))
    }

    private fun onTvShowSelected(tvShow: TvShow) = viewModelScope.launch {

    }


    sealed class Event {
        data class NavigateToMovieDetailsFragment(val movie: Movie) : Event()
        data class NavigateToTvShowDetailsFragment(val tvShow: TvShow) : Event()
    }

    fun onDeleteWatchlist() {
        viewModelScope.launch {
            repository.resetWatchlist()
        }
    }
}
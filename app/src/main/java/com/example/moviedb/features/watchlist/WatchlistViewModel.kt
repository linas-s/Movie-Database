package com.example.moviedb.features.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviedb.data.ListItem
import com.example.moviedb.data.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

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

    fun onDeleteWatchlist() {
        viewModelScope.launch {
            repository.resetWatchlist()
        }
    }
}
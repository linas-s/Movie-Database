package com.example.moviedb.features.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.moviedb.data.*
import com.example.moviedb.features.details.MediaDetailsViewModel
import com.example.moviedb.features.home.HomeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MovieRepository,
    state: SavedStateHandle
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val currentQuery = state.getLiveData<String?>("currentQuery", null)

    val hasCurrentQuery = currentQuery.asFlow().map { it != null }

    private var refreshOnInit = false

    val searchResults = currentQuery.asFlow().flatMapLatest { query ->
        query?.let {
            repository.getSearchResultsPaged(query, refreshOnInit)
        } ?: emptyFlow()
    }.cachedIn(viewModelScope)

    var refreshInProgress = false
    var pendingScrollToTopAfterRefresh = false

    var newQueryInProgress = false
    var pendingScrollToTopAfterNewQuery = false

    fun onSearchQuerySubmit(query: String) {
        refreshOnInit = true
        currentQuery.value = query
        newQueryInProgress = true
        pendingScrollToTopAfterNewQuery = true
    }

    fun onWatchlistClick(searchListItem: SearchListItem) {
        viewModelScope.launch {
            when (searchListItem.mediaType) {
                "movie" -> {
                    val movie = repository.getMovie(searchListItem.id)
                    val currentlyWatchlist = movie.isWatchlist
                    val updatedMovie = movie.copy(isWatchlist = !currentlyWatchlist)
                    repository.updateMovie(updatedMovie)
                }
                "tv" -> {
                    val tvShow = repository.getTvShow(searchListItem.id)
                    val currentlyWatchlist = tvShow.isWatchlist
                    val updatedTvShow = tvShow.copy(isWatchlist = !currentlyWatchlist)
                    repository.updateTvShow(updatedTvShow)
                }
            }
        }
    }

    fun onSearchItemClick(searchListItem: SearchListItem) {
        viewModelScope.launch {
            when (searchListItem.mediaType) {
                "person" -> {
                    onPersonSelected(searchListItem.id)
                }
                else -> {
                    val listItem = ListItem(
                        id = searchListItem.id,
                        title = searchListItem.title,
                        mediaType = searchListItem.mediaType,
                        releaseDate = "",
                        posterPath = searchListItem.posterPath,
                        voteAverage = 0.0,
                        isWatchlist = false,
                        updatedAt = 0,
                        popularity = 0.0,
                        backdropPath = null
                    )
                    onMediaSelected(listItem)
                }
            }
        }
    }

    private fun onMediaSelected(listItem: ListItem) = viewModelScope.launch {
        eventChannel.send(Event.NavigateToDetailsFragment(listItem))
    }

    private fun onPersonSelected(id: Int) = viewModelScope.launch {
        eventChannel.send(Event.NavigateToPersonDetailsFragment(id))
    }

    sealed class Event {
        data class NavigateToDetailsFragment(val listItem: ListItem) : Event()
        data class NavigateToPersonDetailsFragment(val id: Int) : Event()
    }

}
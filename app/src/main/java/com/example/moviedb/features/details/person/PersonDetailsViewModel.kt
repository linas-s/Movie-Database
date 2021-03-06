package com.example.moviedb.features.details.person

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.moviedb.data.ListItem
import com.example.moviedb.data.MovieRepository
import com.example.moviedb.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonDetailsViewModel @Inject constructor(
    private val repository: MovieRepository,
    state: SavedStateHandle
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val refreshTriggerChannel = Channel<Int>()
    private val refreshTrigger =
        refreshTriggerChannel.receiveAsFlow().shareIn(viewModelScope, SharingStarted.Lazily, 1)

    private val receivedPersonId = state.getLiveData<Int>("Id").asFlow()
        .shareIn(viewModelScope, SharingStarted.Lazily, 1)

    val personDetails = refreshTrigger.flatMapLatest { personId ->
        repository.getPersonDetails(personId)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val personMediaCast = refreshTrigger.flatMapLatest { personId ->
        repository.getPersonMediaCast(personId)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val personMediaCrew = refreshTrigger.flatMapLatest { personId ->
        repository.getPersonMediaCrew(personId)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        viewModelScope.launch {
            receivedPersonId.collect { personId ->
                refreshTriggerChannel.send(personId)
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

    fun onRetryButtonClick() {
        viewModelScope.launch {
            receivedPersonId.collect { personId ->
                refreshTriggerChannel.send(personId)
            }
        }
    }

    fun onItemListClick(listItem: ListItem) {
        viewModelScope.launch {
            onMediaSelected(listItem)
        }
    }

    fun onHomepageClick(){
        onHomepageSelected()
    }

    private fun onHomepageSelected() = viewModelScope.launch {
        personDetails.collect { result ->
            if(result is Resource.Success) {
                eventChannel.send(Event.OpenPersonHomepage(result.data?.homepage))
                cancel()
            }
        }
    }

    private fun onMediaSelected(listItem: ListItem) = viewModelScope.launch {
        eventChannel.send(Event.NavigateToMediaDetailsFragment(listItem))
    }

    sealed class Event {
        data class NavigateToMediaDetailsFragment(val listItem: ListItem) : Event()
        data class OpenPersonHomepage(val url: String?) : Event()
    }
}
package com.example.moviedb.features.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.moviedb.data.CastCrewPerson
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
class MediaDetailsViewModel @Inject constructor(
    private val repository: MovieRepository,
    state: SavedStateHandle
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private val receivedMedia = state.getLiveData<ListItem>("listItem").asFlow()
        .shareIn(viewModelScope, SharingStarted.Lazily, 1)

    val media = receivedMedia.flatMapLatest { media ->
        val mediaDetails = if (media.mediaType == "movie") {
            repository.getMovieFlow(media)
        } else {
            repository.getTvShowFlow(media)
        }
        mediaDetails
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val mediaCast = receivedMedia.flatMapLatest { media ->
        repository.getMediaCast(media)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val mediaCrew = receivedMedia.flatMapLatest { media ->
        repository.getMediaCrew(media)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val mediaGenres = receivedMedia.flatMapLatest { media ->
        repository.getMediaGenres(media)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val mediaRecommendations = receivedMedia.flatMapLatest { media ->
        val mediaRecommendations = if (media.mediaType == "movie") {
            repository.getMovieRecommendations(media)
        } else {
            repository.getTvShowRecommendations(media)
        }
        mediaRecommendations
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun getDirectorsCreators(castCrewPersons: List<CastCrewPerson>?): List<CastCrewPerson>? {
        val movieDirectors = castCrewPersons?.filter { person ->
            person.job == "Director"
        }
        val tvShowCreators = castCrewPersons?.filter { person ->
            person.job == "Creator"
        }
        return if (movieDirectors.isNullOrEmpty()) tvShowCreators
        else movieDirectors
    }

    fun onRecommendedListItemClick(listItem: ListItem) {
        viewModelScope.launch {
            onMediaSelected(listItem)
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

    fun onPersonClick(castCrewPerson: CastCrewPerson) {
        onPersonSelected(castCrewPerson.id)
    }

    private fun onMediaSelected(listItem: ListItem) = viewModelScope.launch {
        eventChannel.send(Event.NavigateToMediaDetailsFragment(listItem))
    }

    private fun onPersonSelected(id: Int) = viewModelScope.launch {
        eventChannel.send(Event.NavigateToPersonDetailsFragment(id))
    }

    fun onTrailerClick() {
        viewModelScope.launch {
            onTrailerSelected()
        }
    }

    fun onHomepageClick() {
        viewModelScope.launch {
            onHomepageSelected()
        }
    }

    private fun onTrailerSelected() = viewModelScope.launch {
        receivedMedia.flatMapLatest { media ->
            repository.getMediaVideo(media)
        }.collect { result ->
            if(result is Resource.Success) {
                eventChannel.send(Event.OpenMediaTrailer(result.data?.key))
                cancel()
            }
        }
    }

    private fun onHomepageSelected() = viewModelScope.launch {
        media.collect { result ->
            if(result is Resource.Success) {
                eventChannel.send(Event.OpenMediaHomepage(result.data?.homepage))
                cancel()
            }
        }
    }


    sealed class Event {
        data class NavigateToMediaDetailsFragment(val listItem: ListItem) : Event()
        data class NavigateToPersonDetailsFragment(val id: Int) : Event()
        data class OpenMediaTrailer(val key: String?) : Event()
        data class OpenMediaHomepage(val url: String?) : Event()
    }
}
package com.example.moviedb.features.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.moviedb.data.MovieRepository
import com.example.moviedb.data.SearchListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MovieRepository,
    state: SavedStateHandle
) : ViewModel() {

    private val currentQuery = state.getLiveData<String?>("currentQuery", null)

    private var refreshOnInit = false

    val searchResults = currentQuery.asFlow().flatMapLatest { query ->
        query?.let {
            repository.getSearchResultsPaged(query, refreshOnInit)
        } ?: emptyFlow()
    }.cachedIn(viewModelScope)

    var refreshInProgress = false

    fun onSearchQuerySubmit(query: String){
        refreshOnInit = true
        currentQuery.value = query
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

}
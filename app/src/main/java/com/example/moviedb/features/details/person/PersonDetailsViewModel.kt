package com.example.moviedb.features.details.person

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.moviedb.data.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PersonDetailsViewModel @Inject constructor(
    private val repository: MovieRepository,
    state: SavedStateHandle
) : ViewModel() {

    private val receivedPersonId = state.getLiveData<Int>("Id").asFlow()
        .shareIn(viewModelScope, SharingStarted.Lazily, 1)

    val personDetails = receivedPersonId.flatMapLatest { personId ->
        repository.getPersonDetails(personId)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

}
package com.example.moviedb.features.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.moviedb.data.Movie
import com.example.moviedb.data.MovieRepository
import com.example.moviedb.features.home.HomeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val repository: MovieRepository,
    state: SavedStateHandle
) : ViewModel() {

    private val receivedMovie = state.getLiveData<Movie>("movie").asFlow().shareIn(viewModelScope, SharingStarted.Lazily, 1)

    val movie = receivedMovie.flatMapLatest { movie ->
        repository.getMovieFlow(movie)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val movieCast = receivedMovie.flatMapLatest { movie ->
        repository.getMovieCast(movie)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val movieCrew = receivedMovie.flatMapLatest { movie ->
        repository.getMovieCrew(movie)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val movieGenres = receivedMovie.flatMapLatest { movie ->
        repository.getMovieGenres(movie)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val movieRecommendations = receivedMovie.flatMapLatest { movie ->
        repository.getMovieRecommendations(movie)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)
}
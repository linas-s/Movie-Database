package com.example.moviedb.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Movie::class,
        TvShow::class, Person::class,
        SearchResult::class,
        SearchQueryRemoteKey::class,
        Credits::class,
        MediaGenre::class,
        MediaRecommendation::class
    ],
    version = 1
)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    abstract fun searchQueryRemoteKeyDao(): SearchQueryRemoteKeyDao
}
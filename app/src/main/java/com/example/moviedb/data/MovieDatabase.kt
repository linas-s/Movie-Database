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
        MediaRecommendation::class,
        MediaVideo::class,
        Trending::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    abstract fun searchQueryRemoteKeyDao(): SearchQueryRemoteKeyDao
}
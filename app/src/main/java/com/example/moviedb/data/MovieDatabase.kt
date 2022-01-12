package com.example.moviedb.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Movie::class, TvShow::class], version = 1)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao
}
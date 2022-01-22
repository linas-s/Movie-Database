package com.example.moviedb.data

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "movie")
@Parcelize
data class Movie(
    @PrimaryKey val id: Int,
    val title: String,
    val releaseDate: String,
    val popularity: Double,
    val voteAverage: Double,
    val voteCount: Int,
    val overview: String?,
    val backdropPath: String?,
    val posterPath: String?,
    val homepage: String?,
    val tagline: String?,
    val runtime: Int?,
    val budget: Int?,
    val status: String?,
    val isWatchlist: Boolean,
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable {
    val releaseYear: String get() = releaseDate.take(4)
    val posterUrl: String get() = "https://image.tmdb.org/t/p/w500$posterPath"
    val backdropUrl: String get() = "https://image.tmdb.org/t/p/original$backdropPath"
}

@Entity(tableName = "tv_show")
data class TvShow(
    @PrimaryKey val id: Int,
    val title: String,
    val releaseDate: String,
    val lastAirDate: String?,
    val status: String?,
    val popularity: Double,
    val voteAverage: Double,
    val voteCount: Int,
    val overview: String,
    val backdropPath: String?,
    val posterPath: String?,
    val homepage: String?,
    val isWatchlist: Boolean,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val posterUrl: String get() = "https://image.tmdb.org/t/p/w500$posterPath"
    val backdropUrl: String get() = "https://image.tmdb.org/t/p/w500$backdropPath"
}

data class ListItem(
    val id: Int,
    val title: String,
    val releaseDate: String,
    val posterPath: String?,
    val voteAverage: Double,
    val isWatchlist: Boolean,
    val updatedAt: Long,
    val mediaType: String
) {
    val posterUrl: String get() = "https://image.tmdb.org/t/p/w500$posterPath"
}

@Entity(tableName = "person")
data class Person(
    @PrimaryKey val id: Int,
    val title: String,
    val posterPath: String?,
    val popularity: Double,
    val knownForDepartment: String,
    val birthday: String?,
    val deathDay: String?,
    val homepage: String?
)

@Entity(tableName = "search_results", primaryKeys = ["searchQuery", "mediaType", "id"])
data class SearchResult(
    val searchQuery: String,
    val mediaType: String,
    val id: Int,
    val queryPosition: Int
)

data class SearchListItem(
    val id: Int,
    val mediaType: String,
    val title: String,
    val posterPath: String?,
    val isWatchlist: Boolean?
) {
    val posterUrl: String get() = "https://image.tmdb.org/t/p/w500$posterPath"
}

@Entity(tableName = "credits", primaryKeys = ["mediaType", "personId", "job"])
data class Credits(
    val mediaType: String,
    val mediaId: Int,
    val personId: Int,
    val job: String,
    val character: String?,
    val listOrder: Int
)

data class CastCrewPerson(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val character: String?,
    val job: String?,
    val listOrder: Int,
    val popularity: Double
) {
    val profileUrl: String get() = "https://image.tmdb.org/t/p/w500$posterPath"
    val initials: String
        get() = title
            .split(' ')
            .mapNotNull { it.firstOrNull()?.toString() }
            .reduce { acc, s -> acc + s }
}

@Entity(tableName = "media_genre", primaryKeys = ["mediaType", "id", "genreId"])
data class MediaGenre(
    val mediaType: String,
    val id: Int,
    val genreId: Int,
    val genreName: String
)

@Entity(tableName = "media_recommendation", primaryKeys = ["mediaType", "id", "recommendedId"])
data class MediaRecommendation(
    val mediaType: String,
    val id: Int,
    val recommendedId: Int
)
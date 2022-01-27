package com.example.moviedb.data

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DecimalFormat
import java.text.NumberFormat

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

data class MediaDetails(
    val id: Int,
    val title: String,
    val releaseDate: String,
    val lastAirDate: String?,
    val popularity: Double,
    val voteAverage: Double,
    val voteCount: Int,
    val overview: String,
    val backdropPath: String?,
    val posterPath: String?,
    val homepage: String?,
    val status: String?,
    val budget: Int?,
    val tagline: String?,
    val numberOfSeasons: Int?,
    val mediaType: String,
    val runtime: Int?
) {
    val releaseYear: String get() = releaseDate.take(4)
    private val endingYear: String? get() = lastAirDate?.take(4)
    val runningYears: String get() = if(status == "Returning Series") "$releaseYear-" else "$releaseYear-$endingYear"
    val seasons: String get() = if(numberOfSeasons==1) "$numberOfSeasons season" else "$numberOfSeasons seasons"
    val posterUrl: String get() = "https://image.tmdb.org/t/p/w500$posterPath"
    val backdropUrl: String get() = "https://image.tmdb.org/t/p/original$backdropPath"
    val budgetText: String get() = if(budget == null) "No budget available..." else "$" + NumberFormat.getInstance().format(budget).toString()
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
    val numberOfSeasons: Int?,
    val isWatchlist: Boolean,
    val tagline: String?,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val posterUrl: String get() = "https://image.tmdb.org/t/p/w500$posterPath"
    val backdropUrl: String get() = "https://image.tmdb.org/t/p/w500$backdropPath"
}

@Parcelize
data class ListItem(
    val id: Int,
    val title: String,
    val releaseDate: String,
    val posterPath: String?,
    val voteAverage: Double,
    val isWatchlist: Boolean,
    val popularity: Double,
    val updatedAt: Long,
    val mediaType: String
) : Parcelable {
    val posterUrl: String get() = "https://image.tmdb.org/t/p/w500$posterPath"
    val voteAverageRounded: String get() = DecimalFormat("#.#").format(voteAverage)
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
    val homepage: String?,
    val biography: String?,
    val placeOfBirth: String?
) {
    val profileUrl: String get() = "https://image.tmdb.org/t/p/w500$posterPath"
}

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

@Entity(tableName = "credits", primaryKeys = ["mediaType", "mediaId", "personId", "job"])
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
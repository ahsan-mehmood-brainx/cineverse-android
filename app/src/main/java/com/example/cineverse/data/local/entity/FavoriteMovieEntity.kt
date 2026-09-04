package com.example.cineverse.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A denormalized snapshot of a [com.example.cineverse.domain.model.Movie] taken at the moment
 * it was favorited, so the Favorites screen renders fully offline without depending on the
 * TMDB API or process-lifetime genre-name cache being available.
 */
@Entity(tableName = "favorite_movies")
data class FavoriteMovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val posterUrl: String?,
    val rating: Double,
    val releaseDate: String,
    val genre: String,
    val overview: String,
    val addedAtEpochMillis: Long
)

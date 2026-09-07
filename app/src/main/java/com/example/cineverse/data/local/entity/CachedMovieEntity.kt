package com.example.cineverse.data.local.entity

import androidx.room.Entity

/**
 * A denormalized snapshot of a movie-list item, keyed by which list ([category]) it came from
 * and its [position] in that list, so a category's feed can be replayed offline in its original
 * order when the network is unavailable.
 */
@Entity(tableName = "cached_movies", primaryKeys = ["category", "id"])
data class CachedMovieEntity(
    val category: String,
    val id: Int,
    val position: Int,
    val title: String,
    val posterUrl: String?,
    val rating: Double,
    val releaseDate: String,
    val genre: String,
    val overview: String
)

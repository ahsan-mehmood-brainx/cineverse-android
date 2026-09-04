package com.example.cineverse.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieDetailDto(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "vote_average") val voteAverage: Double = 0.0,
    @Json(name = "release_date") val releaseDate: String? = null,
    @Json(name = "overview") val overview: String = "",
    @Json(name = "genres") val genres: List<GenreDto> = emptyList()
)

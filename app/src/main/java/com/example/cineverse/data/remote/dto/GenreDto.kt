package com.example.cineverse.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenreDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String
)

@JsonClass(generateAdapter = true)
data class GenresResponseDto(
    @Json(name = "genres") val genres: List<GenreDto> = emptyList()
)

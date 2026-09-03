package com.example.cineverse.domain.model

/**
 * UI-facing movie model. Currently populated from [com.example.cineverse.ui.home.HomeMockData];
 * once the TMDB repository lands, a mapper in data/mapper will build this from the remote DTO.
 */
data class Movie(
    val id: Int,
    val title: String,
    val posterUrl: String?,
    val rating: Double,
    val releaseDate: String,
    val genre: String,
    val overview: String
)

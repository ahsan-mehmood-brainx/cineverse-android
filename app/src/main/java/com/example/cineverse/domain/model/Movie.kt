package com.example.cineverse.domain.model

/** UI-facing movie model, built from remote DTOs by the mappers in data/mapper. */
data class Movie(
    val id: Int,
    val title: String,
    val posterUrl: String?,
    val rating: Double,
    val releaseDate: String,
    val genre: String,
    val overview: String
)

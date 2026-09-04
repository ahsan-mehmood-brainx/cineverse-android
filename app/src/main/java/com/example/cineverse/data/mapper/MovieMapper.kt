package com.example.cineverse.data.mapper

import com.example.cineverse.data.remote.dto.MovieDetailDto
import com.example.cineverse.data.remote.dto.MovieDto
import com.example.cineverse.domain.model.Movie
import com.example.cineverse.util.Constants

fun MovieDto.toDomain(genreNamesById: Map<Int, String>): Movie = Movie(
    id = id,
    title = title,
    posterUrl = posterPath?.toFullPosterUrl(),
    rating = voteAverage,
    releaseDate = releaseDate.orEmpty(),
    genre = genreIds.mapNotNull { genreNamesById[it] }.joinToString(", "),
    overview = overview
)

fun MovieDetailDto.toDomain(): Movie = Movie(
    id = id,
    title = title,
    posterUrl = posterPath?.toFullPosterUrl(),
    rating = voteAverage,
    releaseDate = releaseDate.orEmpty(),
    genre = genres.joinToString(", ") { it.name },
    overview = overview
)

private fun String.toFullPosterUrl(): String = Constants.TMDB_IMAGE_BASE_URL + removePrefix("/")

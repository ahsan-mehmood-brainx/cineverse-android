package com.example.cineverse.domain.repository

import com.example.cineverse.domain.model.Genre
import com.example.cineverse.domain.model.Movie
import com.example.cineverse.util.Resource

interface MovieRepository {
    suspend fun getTrendingMovies(): Resource<List<Movie>>
    suspend fun getPopularMovies(): Resource<List<Movie>>
    suspend fun getTopRatedMovies(): Resource<List<Movie>>
    suspend fun getNowPlayingMovies(): Resource<List<Movie>>
    suspend fun getUpcomingMovies(): Resource<List<Movie>>
    suspend fun getGenres(): Resource<List<Genre>>
    suspend fun searchMovies(query: String): Resource<List<Movie>>
    suspend fun getMovieDetail(movieId: Int): Resource<Movie>
}

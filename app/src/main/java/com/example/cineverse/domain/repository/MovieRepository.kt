package com.example.cineverse.domain.repository

import com.example.cineverse.domain.model.Genre
import com.example.cineverse.domain.model.Movie
import com.example.cineverse.util.Resource
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getTrendingMovies(): Resource<List<Movie>>
    suspend fun getPopularMovies(): Resource<List<Movie>>
    suspend fun getTopRatedMovies(): Resource<List<Movie>>
    suspend fun getNowPlayingMovies(): Resource<List<Movie>>
    suspend fun getUpcomingMovies(): Resource<List<Movie>>
    suspend fun getGenres(): Resource<List<Genre>>
    suspend fun searchMovies(query: String): Resource<List<Movie>>
    suspend fun getMovieDetail(movieId: Int): Resource<Movie>

    /** Favorites are cached locally in Room so they're available offline. */
    fun observeFavorites(): Flow<List<Movie>>
    fun observeIsFavorite(movieId: Int): Flow<Boolean>
    suspend fun addFavorite(movie: Movie)
    suspend fun removeFavorite(movieId: Int)
    suspend fun clearFavorites()
}

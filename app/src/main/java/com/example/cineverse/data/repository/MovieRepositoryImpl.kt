package com.example.cineverse.data.repository

import com.example.cineverse.data.local.dao.FavoriteMovieDao
import com.example.cineverse.data.mapper.toDomain
import com.example.cineverse.data.mapper.toFavoriteEntity
import com.example.cineverse.data.remote.api.TMDBApi
import com.example.cineverse.data.remote.dto.MovieDto
import com.example.cineverse.domain.model.Genre
import com.example.cineverse.domain.model.Movie
import com.example.cineverse.domain.repository.MovieRepository
import com.example.cineverse.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val api: TMDBApi,
    private val favoriteMovieDao: FavoriteMovieDao
) : MovieRepository {

    private val genreCacheMutex = Mutex()
    private var cachedGenreNamesById: Map<Int, String>? = null

    override suspend fun getTrendingMovies(): Resource<List<Movie>> = safeCall {
        api.getTrendingMovies().results.toDomainMovies()
    }

    override suspend fun getPopularMovies(): Resource<List<Movie>> = safeCall {
        api.getPopularMovies().results.toDomainMovies()
    }

    override suspend fun getTopRatedMovies(): Resource<List<Movie>> = safeCall {
        api.getTopRatedMovies().results.toDomainMovies()
    }

    override suspend fun getNowPlayingMovies(): Resource<List<Movie>> = safeCall {
        api.getNowPlayingMovies().results.toDomainMovies()
    }

    override suspend fun getUpcomingMovies(): Resource<List<Movie>> = safeCall {
        api.getUpcomingMovies().results.toDomainMovies()
    }

    override suspend fun getGenres(): Resource<List<Genre>> = safeCall {
        genreNamesById().map { (id, name) -> Genre(id, name) }
    }

    override suspend fun searchMovies(query: String): Resource<List<Movie>> = safeCall {
        api.searchMovies(query).results.toDomainMovies()
    }

    override suspend fun getMovieDetail(movieId: Int): Resource<Movie> = safeCall {
        api.getMovieDetail(movieId).toDomain()
    }

    override fun observeFavorites(): Flow<List<Movie>> =
        favoriteMovieDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeIsFavorite(movieId: Int): Flow<Boolean> =
        favoriteMovieDao.observeIsFavorite(movieId)

    override suspend fun addFavorite(movie: Movie) {
        favoriteMovieDao.insert(movie.toFavoriteEntity(addedAtEpochMillis = System.currentTimeMillis()))
    }

    override suspend fun removeFavorite(movieId: Int) {
        favoriteMovieDao.deleteById(movieId)
    }

    override suspend fun clearFavorites() {
        favoriteMovieDao.clearAll()
    }

    private suspend fun List<MovieDto>.toDomainMovies(): List<Movie> {
        val genreNamesById = genreNamesById()
        return map { it.toDomain(genreNamesById) }
    }

    /** Genres rarely change, so the id-to-name lookup used to label movie list items is cached for the process lifetime. */
    private suspend fun genreNamesById(): Map<Int, String> {
        cachedGenreNamesById?.let { return it }
        return genreCacheMutex.withLock {
            cachedGenreNamesById ?: api.getGenres().genres.associate { it.id to it.name }
                .also { cachedGenreNamesById = it }
        }
    }

    private suspend inline fun <T> safeCall(block: suspend () -> T): Resource<T> = try {
        Resource.Success(block())
    } catch (e: IOException) {
        Resource.Error("Couldn't connect. Check your internet connection.", e)
    } catch (e: HttpException) {
        Resource.Error("Something went wrong (code ${e.code()}).", e)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Unexpected error.", e)
    }
}

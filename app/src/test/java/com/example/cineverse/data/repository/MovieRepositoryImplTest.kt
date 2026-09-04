package com.example.cineverse.data.repository

import com.example.cineverse.data.local.dao.FavoriteMovieDao
import com.example.cineverse.data.local.entity.FavoriteMovieEntity
import com.example.cineverse.data.remote.api.TMDBApi
import com.example.cineverse.data.remote.dto.GenreDto
import com.example.cineverse.data.remote.dto.GenresResponseDto
import com.example.cineverse.data.remote.dto.MovieDetailDto
import com.example.cineverse.data.remote.dto.MovieDto
import com.example.cineverse.data.remote.dto.MoviesResponseDto
import com.example.cineverse.domain.model.Movie
import com.example.cineverse.util.Resource
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class MovieRepositoryImplTest {

    private val api: TMDBApi = mock()

    /** In-memory fake so favorites tests exercise real insert/delete/query semantics. */
    private class FakeFavoriteMovieDao : FavoriteMovieDao {
        val state = MutableStateFlow<List<FavoriteMovieEntity>>(emptyList())

        override fun observeAll() = state
        override fun observeIsFavorite(movieId: Int) = state.map { list -> list.any { it.id == movieId } }
        override suspend fun insert(entity: FavoriteMovieEntity) {
            state.value = state.value.filterNot { it.id == entity.id } + entity
        }

        override suspend fun deleteById(movieId: Int) {
            state.value = state.value.filterNot { it.id == movieId }
        }
    }

    private val favoriteMovieDao = FakeFavoriteMovieDao()
    private val repository = MovieRepositoryImpl(api, favoriteMovieDao)

    private val genresResponse = GenresResponseDto(
        genres = listOf(GenreDto(1, "Action"), GenreDto(5, "Sci-Fi"))
    )

    @Test
    fun getPopularMovies_success_mapsGenreIdsUsingGenreLookup() = runTest {
        whenever(api.getGenres()).thenReturn(genresResponse)
        whenever(api.getPopularMovies(any())).thenReturn(
            MoviesResponseDto(listOf(MovieDto(id = 1, title = "Dune", genreIds = listOf(1, 5))))
        )

        val result = repository.getPopularMovies()

        assertThat(result).isInstanceOf(Resource.Success::class.java)
        val movies = (result as Resource.Success).data
        assertThat(movies).hasSize(1)
        assertThat(movies.first().genre).isEqualTo("Action, Sci-Fi")
    }

    @Test
    fun getGenres_cachesAcrossCalls_apiHitOnlyOnce() = runTest {
        whenever(api.getGenres()).thenReturn(genresResponse)
        whenever(api.getPopularMovies(any())).thenReturn(
            MoviesResponseDto(listOf(MovieDto(id = 1, title = "Dune", genreIds = listOf(1))))
        )

        repository.getGenres()
        repository.getPopularMovies()
        repository.getPopularMovies()

        verify(api, times(1)).getGenres()
    }

    @Test
    fun getTrendingMovies_ioException_mapsToNetworkError() = runTest {
        whenever(api.getTrendingMovies()).thenAnswer { throw IOException("no connection") }

        val result = repository.getTrendingMovies()

        assertThat(result).isInstanceOf(Resource.Error::class.java)
        assertThat((result as Resource.Error).message).contains("internet connection")
    }

    @Test
    fun getTrendingMovies_httpException_mapsToServerError() = runTest {
        val httpException = HttpException(
            Response.error<Any>(404, "not found".toResponseBody("text/plain".toMediaType()))
        )
        whenever(api.getTrendingMovies()).thenAnswer { throw httpException }

        val result = repository.getTrendingMovies()

        assertThat(result).isInstanceOf(Resource.Error::class.java)
        assertThat((result as Resource.Error).message).contains("404")
    }

    @Test
    fun searchMovies_success_returnsMappedMovies() = runTest {
        whenever(api.getGenres()).thenReturn(genresResponse)
        whenever(api.searchMovies(any(), any())).thenReturn(
            MoviesResponseDto(listOf(MovieDto(id = 7, title = "Arrival", genreIds = listOf(5))))
        )

        val result = repository.searchMovies("arrival")

        assertThat(result).isInstanceOf(Resource.Success::class.java)
        assertThat((result as Resource.Success).data.first().title).isEqualTo("Arrival")
    }

    @Test
    fun getMovieDetail_success_mapsGenresDirectly() = runTest {
        whenever(api.getMovieDetail(any())).thenReturn(
            MovieDetailDto(id = 1, title = "Dune", genres = listOf(GenreDto(5, "Sci-Fi")))
        )

        val result = repository.getMovieDetail(1)

        assertThat(result).isInstanceOf(Resource.Success::class.java)
        assertThat((result as Resource.Success).data.genre).isEqualTo("Sci-Fi")
    }

    @Test
    fun addFavorite_thenObserveFavorites_containsMovie() = runTest {
        val movie = Movie(1, "Dune", null, 8.3, "2021-10-22", "Sci-Fi", "A noble heir.")

        repository.addFavorite(movie)

        val favorites = repository.observeFavorites().first()
        assertThat(favorites).containsExactly(movie)
    }

    @Test
    fun addFavorite_sameMovieTwice_doesNotDuplicate() = runTest {
        val movie = Movie(1, "Dune", null, 8.3, "2021-10-22", "Sci-Fi", "A noble heir.")

        repository.addFavorite(movie)
        repository.addFavorite(movie)

        assertThat(repository.observeFavorites().first()).hasSize(1)
    }

    @Test
    fun removeFavorite_removesFromFavorites() = runTest {
        val movie = Movie(1, "Dune", null, 8.3, "2021-10-22", "Sci-Fi", "A noble heir.")
        repository.addFavorite(movie)

        repository.removeFavorite(movie.id)

        assertThat(repository.observeFavorites().first()).isEmpty()
    }

    @Test
    fun observeIsFavorite_reflectsCurrentState() = runTest {
        val movie = Movie(1, "Dune", null, 8.3, "2021-10-22", "Sci-Fi", "A noble heir.")

        assertThat(repository.observeIsFavorite(movie.id).first()).isFalse()

        repository.addFavorite(movie)
        assertThat(repository.observeIsFavorite(movie.id).first()).isTrue()

        repository.removeFavorite(movie.id)
        assertThat(repository.observeIsFavorite(movie.id).first()).isFalse()
    }
}

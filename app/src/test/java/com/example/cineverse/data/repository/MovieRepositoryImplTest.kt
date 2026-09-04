package com.example.cineverse.data.repository

import com.example.cineverse.data.remote.api.TMDBApi
import com.example.cineverse.data.remote.dto.GenreDto
import com.example.cineverse.data.remote.dto.GenresResponseDto
import com.example.cineverse.data.remote.dto.MovieDetailDto
import com.example.cineverse.data.remote.dto.MovieDto
import com.example.cineverse.data.remote.dto.MoviesResponseDto
import com.example.cineverse.util.Resource
import com.google.common.truth.Truth.assertThat
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
    private val repository = MovieRepositoryImpl(api)

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
}

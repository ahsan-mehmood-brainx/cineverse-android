package com.example.cineverse.data.remote.api

import com.example.cineverse.data.remote.dto.GenresResponseDto
import com.example.cineverse.data.remote.dto.MovieDetailDto
import com.example.cineverse.data.remote.dto.MoviesResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Retrofit endpoints for The Movie DB API. Auth is handled by [com.example.cineverse.data.remote.interceptor.ApiKeyInterceptor]. */
interface TMDBApi {

    @GET("trending/movie/day")
    suspend fun getTrendingMovies(): MoviesResponseDto

    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("page") page: Int = 1): MoviesResponseDto

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("page") page: Int = 1): MoviesResponseDto

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(@Query("page") page: Int = 1): MoviesResponseDto

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(@Query("page") page: Int = 1): MoviesResponseDto

    @GET("genre/movie/list")
    suspend fun getGenres(): GenresResponseDto

    @GET("search/movie")
    suspend fun searchMovies(@Query("query") query: String, @Query("page") page: Int = 1): MoviesResponseDto

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(@Path("movie_id") movieId: Int): MovieDetailDto
}

package com.example.cineverse.util

import com.example.cineverse.BuildConfig

object Constants {
    const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500/"

    /** Populated at build time from local.properties#TMDB_API_KEY — see README. */
    val TMDB_API_KEY: String = BuildConfig.TMDB_API_KEY

    const val DATABASE_NAME = "cineverse.db"

    const val SYNC_WORK_NAME = "cineverse_periodic_sync"
    const val SYNC_PREFS_NAME = "cineverse_sync_prefs"
    const val KEY_LAST_TOP_POPULAR_MOVIE_ID = "last_top_popular_movie_id"

    const val NOTIFICATION_CHANNEL_ID = "popular_movies"
}

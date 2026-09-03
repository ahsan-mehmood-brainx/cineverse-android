package com.example.cineverse.util

import com.example.cineverse.BuildConfig

object Constants {
    const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500/"

    /** Populated at build time from local.properties#TMDB_API_KEY — see README. */
    val TMDB_API_KEY: String = BuildConfig.TMDB_API_KEY

    const val DATABASE_NAME = "cineverse.db"

    const val SYNC_WORK_NAME = "cineverse_periodic_sync"
}

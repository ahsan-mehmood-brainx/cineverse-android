package com.example.cineverse.util

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Tracks the small bit of state [com.example.cineverse.worker.MovieSyncWorker] needs across runs. */
@Singleton
class SyncPreferences @Inject constructor(@ApplicationContext context: Context) {

    private val prefs = context.getSharedPreferences(Constants.SYNC_PREFS_NAME, Context.MODE_PRIVATE)

    var lastTopPopularMovieId: Int?
        get() = prefs.getInt(Constants.KEY_LAST_TOP_POPULAR_MOVIE_ID, NOT_SET).takeIf { it != NOT_SET }
        set(value) = prefs.edit { putInt(Constants.KEY_LAST_TOP_POPULAR_MOVIE_ID, value ?: NOT_SET) }

    private companion object {
        const val NOT_SET = -1
    }
}

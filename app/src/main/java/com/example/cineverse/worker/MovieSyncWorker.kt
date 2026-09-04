package com.example.cineverse.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.cineverse.domain.repository.MovieRepository
import com.example.cineverse.util.NotificationHelper
import com.example.cineverse.util.Resource
import com.example.cineverse.util.SyncPreferences
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException

/**
 * Refreshes the popular-movies ranking once a day. If the movie at the top of the list has
 * changed since the last successful sync, the user gets a notification (see [NotificationHelper]
 * — itself a no-op if notifications aren't enabled/permitted). Silent on the very first run,
 * since there's nothing yet to compare against.
 */
@HiltWorker
class MovieSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: MovieRepository,
    private val syncPreferences: SyncPreferences,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return when (val result = repository.getPopularMovies()) {
            is Resource.Success -> {
                val topMovie = result.data.firstOrNull() ?: return Result.success()
                val previousTopId = syncPreferences.lastTopPopularMovieId

                if (hasNewTopMovie(previousTopId, topMovie.id)) {
                    notificationHelper.showNewPopularMovieNotification(topMovie)
                }
                syncPreferences.lastTopPopularMovieId = topMovie.id
                Result.success()
            }
            is Resource.Error -> if (result.throwable is IOException) Result.retry() else Result.failure()
            is Resource.Loading -> Result.success()
        }
    }
}

/** True only once we have a prior sync to compare against and the top spot actually changed. */
internal fun hasNewTopMovie(previousTopId: Int?, currentTopId: Int): Boolean =
    previousTopId != null && previousTopId != currentTopId

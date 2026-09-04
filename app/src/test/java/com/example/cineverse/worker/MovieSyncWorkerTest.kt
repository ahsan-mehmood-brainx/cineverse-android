package com.example.cineverse.worker

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MovieSyncWorkerTest {

    @Test
    fun hasNewTopMovie_firstRun_noPreviousValue_returnsFalse() {
        assertThat(hasNewTopMovie(previousTopId = null, currentTopId = 42)).isFalse()
    }

    @Test
    fun hasNewTopMovie_topUnchanged_returnsFalse() {
        assertThat(hasNewTopMovie(previousTopId = 42, currentTopId = 42)).isFalse()
    }

    @Test
    fun hasNewTopMovie_topChanged_returnsTrue() {
        assertThat(hasNewTopMovie(previousTopId = 42, currentTopId = 7)).isTrue()
    }
}

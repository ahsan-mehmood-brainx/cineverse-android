package com.example.cineverse.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cineverse.data.local.MovieDatabase
import com.example.cineverse.data.local.entity.ProfileEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileDaoTest {

    private lateinit var database: MovieDatabase
    private lateinit var dao: ProfileDao

    private fun entity(displayName: String, bio: String) = ProfileEntity(
        displayName = displayName,
        bio = bio,
        username = "user",
        email = "user@example.com",
        profileImageUri = null
    )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(context, MovieDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.profileDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun observe_beforeAnyRowExists_emitsNull() = runBlocking {
        assertThat(dao.observe().first()).isNull()
    }

    @Test
    fun upsert_thenObserve_emitsInsertedEntity() = runBlocking {
        dao.upsert(entity(displayName = "Alice", bio = "Bio"))

        val profile = dao.observe().first()

        assertThat(profile).isNotNull()
        assertThat(profile?.displayName).isEqualTo("Alice")
        assertThat(profile?.bio).isEqualTo("Bio")
    }

    @Test
    fun upsert_calledAgain_replacesRatherThanDuplicates() = runBlocking {
        dao.upsert(entity(displayName = "Alice", bio = "Bio"))
        dao.upsert(entity(displayName = "Bob", bio = "Updated bio"))

        val profile = dao.observe().first()

        assertThat(profile?.id).isEqualTo(ProfileEntity.SINGLETON_ID)
        assertThat(profile?.displayName).isEqualTo("Bob")
        assertThat(profile?.bio).isEqualTo("Updated bio")
    }
}

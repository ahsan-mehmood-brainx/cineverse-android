package com.example.cineverse.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cineverse.data.local.MovieDatabase
import com.example.cineverse.data.local.entity.FavoriteMovieEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteMovieDaoTest {

    private lateinit var database: MovieDatabase
    private lateinit var dao: FavoriteMovieDao

    private fun entity(id: Int, addedAt: Long = 0L) = FavoriteMovieEntity(
        id = id,
        title = "Movie $id",
        posterUrl = null,
        rating = 7.5,
        releaseDate = "2024-01-01",
        genre = "Drama",
        overview = "Overview",
        addedAtEpochMillis = addedAt
    )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(context, MovieDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.favoriteMovieDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insert_thenObserveAll_returnsInsertedMovie() = runBlocking {
        dao.insert(entity(id = 1))

        val favorites = dao.observeAll().first()

        assertThat(favorites).hasSize(1)
        assertThat(favorites.first().id).isEqualTo(1)
    }

    @Test
    fun observeAll_ordersByMostRecentlyAddedFirst() = runBlocking {
        dao.insert(entity(id = 1, addedAt = 100L))
        dao.insert(entity(id = 2, addedAt = 200L))

        val favorites = dao.observeAll().first()

        assertThat(favorites.map { it.id }).containsExactly(2, 1).inOrder()
    }

    @Test
    fun insert_sameIdTwice_replacesRatherThanDuplicates() = runBlocking {
        dao.insert(entity(id = 1))
        dao.insert(entity(id = 1))

        assertThat(dao.observeAll().first()).hasSize(1)
    }

    @Test
    fun deleteById_removesMovie() = runBlocking {
        dao.insert(entity(id = 1))

        dao.deleteById(1)

        assertThat(dao.observeAll().first()).isEmpty()
    }

    @Test
    fun observeIsFavorite_reflectsPresence() = runBlocking {
        assertThat(dao.observeIsFavorite(1).first()).isFalse()

        dao.insert(entity(id = 1))
        assertThat(dao.observeIsFavorite(1).first()).isTrue()

        dao.deleteById(1)
        assertThat(dao.observeIsFavorite(1).first()).isFalse()
    }
}

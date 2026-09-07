package com.example.cineverse.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cineverse.data.local.MovieDatabase
import com.example.cineverse.data.local.entity.CachedMovieEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieCacheDaoTest {

    private lateinit var database: MovieDatabase
    private lateinit var dao: MovieCacheDao

    private fun entity(
        category: String,
        id: Int,
        position: Int,
        title: String = "Movie $id"
    ) = CachedMovieEntity(
        category = category,
        id = id,
        position = position,
        title = title,
        posterUrl = null,
        rating = 7.5,
        releaseDate = "2024-01-01",
        genre = "Drama",
        overview = "Overview"
    )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(context, MovieDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.movieCacheDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getByCategory_withNothingCached_returnsEmptyList() = runBlocking {
        assertThat(dao.getByCategory("popular")).isEmpty()
    }

    @Test
    fun insertAll_thenGetByCategory_returnsItemsOrderedByPosition() = runBlocking {
        dao.insertAll(
            listOf(
                entity(category = "popular", id = 2, position = 1),
                entity(category = "popular", id = 1, position = 0)
            )
        )

        val cached = dao.getByCategory("popular")

        assertThat(cached.map { it.id }).containsExactly(1, 2).inOrder()
    }

    @Test
    fun insertAll_sameCategoryAndId_replacesRatherThanDuplicates() = runBlocking {
        dao.insertAll(listOf(entity(category = "popular", id = 1, position = 0, title = "Original")))
        dao.insertAll(listOf(entity(category = "popular", id = 1, position = 0, title = "Updated")))

        val cached = dao.getByCategory("popular")

        assertThat(cached).hasSize(1)
        assertThat(cached.first().title).isEqualTo("Updated")
    }

    @Test
    fun clearCategory_removesOnlyThatCategory() = runBlocking {
        dao.insertAll(listOf(entity(category = "popular", id = 1, position = 0)))
        dao.insertAll(listOf(entity(category = "top_rated", id = 2, position = 0)))

        dao.clearCategory("popular")

        assertThat(dao.getByCategory("popular")).isEmpty()
        assertThat(dao.getByCategory("top_rated")).hasSize(1)
    }
}

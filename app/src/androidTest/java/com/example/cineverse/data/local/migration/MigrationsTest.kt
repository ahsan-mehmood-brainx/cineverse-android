package com.example.cineverse.data.local.migration

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.cineverse.data.local.MovieDatabase
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationsTest {

    private val testDbName = "migration-test.db"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MovieDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate1To2_preservesExistingProfileRow_andAddsDefaultColumns() {
        helper.createDatabase(testDbName, 1).apply {
            execSQL("INSERT INTO profile (id, displayName, bio) VALUES (0, 'Alice', 'Bio')")
            close()
        }

        val migratedDb = helper.runMigrationsAndValidate(testDbName, 2, true, MIGRATION_1_2)

        val cursor = migratedDb.query("SELECT displayName, bio, username, email, profileImageUri FROM profile WHERE id = 0")
        cursor.use {
            assertThat(it.moveToFirst()).isTrue()
            assertThat(it.getString(it.getColumnIndexOrThrow("displayName"))).isEqualTo("Alice")
            assertThat(it.getString(it.getColumnIndexOrThrow("bio"))).isEqualTo("Bio")
            assertThat(it.getString(it.getColumnIndexOrThrow("username"))).isEqualTo("")
            assertThat(it.getString(it.getColumnIndexOrThrow("email"))).isEqualTo("")
            assertThat(it.isNull(it.getColumnIndexOrThrow("profileImageUri"))).isTrue()
        }
    }

    @Test
    fun migrate1To2_createsCachedMoviesTable_acceptingFullColumnSet() {
        helper.createDatabase(testDbName, 1).close()

        val migratedDb = helper.runMigrationsAndValidate(testDbName, 2, true, MIGRATION_1_2)

        migratedDb.execSQL(
            """
            INSERT INTO cached_movies
                (category, id, position, title, posterUrl, rating, releaseDate, genre, overview)
            VALUES
                ('popular', 1, 0, 'Movie 1', 'http://poster', 7.5, '2024-01-01', 'Drama', 'Overview')
            """.trimIndent()
        )

        val cursor = migratedDb.query("SELECT * FROM cached_movies WHERE category = 'popular' AND id = 1")
        cursor.use {
            assertThat(it.moveToFirst()).isTrue()
            assertThat(it.getString(it.getColumnIndexOrThrow("title"))).isEqualTo("Movie 1")
            assertThat(it.getInt(it.getColumnIndexOrThrow("position"))).isEqualTo(0)
        }
    }

    @Test
    fun migrate1To2_thenOpenWithRoom_validatesSchemaSuccessfully() {
        helper.createDatabase(testDbName, 1).apply {
            execSQL("INSERT INTO profile (id, displayName, bio) VALUES (0, 'Alice', 'Bio')")
            close()
        }

        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val database = Room.databaseBuilder(context, MovieDatabase::class.java, testDbName)
            .addMigrations(MIGRATION_1_2)
            .build()

        // Room validates the migrated schema against the @Entity definitions on open.
        database.openHelper.readableDatabase
        database.close()
    }
}

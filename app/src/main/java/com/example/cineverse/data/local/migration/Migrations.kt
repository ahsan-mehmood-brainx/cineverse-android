package com.example.cineverse.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Adds the [com.example.cineverse.data.local.entity.CachedMovieEntity] table (offline cache for
 * movie-list feeds) and the username/email/profileImageUri columns on `profile`.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `cached_movies` (
                `category` TEXT NOT NULL,
                `id` INTEGER NOT NULL,
                `position` INTEGER NOT NULL,
                `title` TEXT NOT NULL,
                `posterUrl` TEXT,
                `rating` REAL NOT NULL,
                `releaseDate` TEXT NOT NULL,
                `genre` TEXT NOT NULL,
                `overview` TEXT NOT NULL,
                PRIMARY KEY(`category`, `id`)
            )
            """.trimIndent()
        )
        db.execSQL("ALTER TABLE `profile` ADD COLUMN `username` TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE `profile` ADD COLUMN `email` TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE `profile` ADD COLUMN `profileImageUri` TEXT")
    }
}

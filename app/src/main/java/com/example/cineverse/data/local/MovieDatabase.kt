package com.example.cineverse.data.local

import androidx.room.RoomDatabase

/**
 * Room database skeleton. Room requires at least one @Entity before the
 * @Database annotation can be added, so this is wired up once the first
 * entity/DAO lands in data/local/entity and data/local/dao, e.g.:
 *
 * @Database(entities = [FavoriteMovieEntity::class], version = 1, exportSchema = true)
 * abstract class MovieDatabase : RoomDatabase() {
 *     abstract fun favoriteMovieDao(): FavoriteMovieDao
 * }
 */
abstract class MovieDatabase : RoomDatabase()

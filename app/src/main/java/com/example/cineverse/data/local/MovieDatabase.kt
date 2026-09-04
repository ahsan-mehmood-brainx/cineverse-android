package com.example.cineverse.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cineverse.data.local.dao.FavoriteMovieDao
import com.example.cineverse.data.local.dao.ProfileDao
import com.example.cineverse.data.local.entity.FavoriteMovieEntity
import com.example.cineverse.data.local.entity.ProfileEntity

@Database(
    entities = [FavoriteMovieEntity::class, ProfileEntity::class],
    version = 1,
    exportSchema = true
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun favoriteMovieDao(): FavoriteMovieDao
    abstract fun profileDao(): ProfileDao
}

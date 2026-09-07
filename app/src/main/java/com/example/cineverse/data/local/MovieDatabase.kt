package com.example.cineverse.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cineverse.data.local.dao.FavoriteMovieDao
import com.example.cineverse.data.local.dao.MovieCacheDao
import com.example.cineverse.data.local.dao.ProfileDao
import com.example.cineverse.data.local.entity.CachedMovieEntity
import com.example.cineverse.data.local.entity.FavoriteMovieEntity
import com.example.cineverse.data.local.entity.ProfileEntity

@Database(
    entities = [FavoriteMovieEntity::class, ProfileEntity::class, CachedMovieEntity::class],
    version = 2,
    exportSchema = true
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun favoriteMovieDao(): FavoriteMovieDao
    abstract fun profileDao(): ProfileDao
    abstract fun movieCacheDao(): MovieCacheDao
}

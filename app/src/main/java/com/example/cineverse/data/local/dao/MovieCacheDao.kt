package com.example.cineverse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cineverse.data.local.entity.CachedMovieEntity

@Dao
interface MovieCacheDao {

    @Query("SELECT * FROM cached_movies WHERE category = :category ORDER BY position ASC")
    suspend fun getByCategory(category: String): List<CachedMovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<CachedMovieEntity>)

    @Query("DELETE FROM cached_movies WHERE category = :category")
    suspend fun clearCategory(category: String)
}

package com.example.cineverse.di

import android.content.Context
import androidx.room.Room
import com.example.cineverse.data.local.MovieDatabase
import com.example.cineverse.data.local.dao.FavoriteMovieDao
import com.example.cineverse.data.local.dao.ProfileDao
import com.example.cineverse.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMovieDatabase(@ApplicationContext context: Context): MovieDatabase =
        Room.databaseBuilder(context, MovieDatabase::class.java, Constants.DATABASE_NAME).build()

    @Provides
    fun provideFavoriteMovieDao(database: MovieDatabase): FavoriteMovieDao = database.favoriteMovieDao()

    @Provides
    fun provideProfileDao(database: MovieDatabase): ProfileDao = database.profileDao()
}

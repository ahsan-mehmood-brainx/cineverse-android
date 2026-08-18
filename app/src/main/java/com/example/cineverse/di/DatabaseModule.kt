package com.example.cineverse.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Provides the Room database and its DAOs. Left empty until MovieDatabase has at
 * least one @Entity/@Dao (see data/local/MovieDatabase.kt), e.g.:
 *
 * @Module
 * @InstallIn(SingletonComponent::class)
 * object DatabaseModule {
 *     @Provides
 *     @Singleton
 *     fun provideMovieDatabase(@ApplicationContext context: Context): MovieDatabase =
 *         Room.databaseBuilder(context, MovieDatabase::class.java, Constants.DATABASE_NAME).build()
 *
 *     @Provides
 *     fun provideFavoriteMovieDao(database: MovieDatabase): FavoriteMovieDao = database.favoriteMovieDao()
 * }
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule

package com.example.cineverse.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Binds domain/repository interfaces to their data/repository implementations.
 * Left empty until the first repository is implemented, e.g.:
 *
 * @Module
 * @InstallIn(SingletonComponent::class)
 * abstract class RepositoryModule {
 *     @Binds
 *     abstract fun bindMovieRepository(impl: MovieRepositoryImpl): MovieRepository
 * }
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule

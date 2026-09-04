package com.example.cineverse.di

import com.example.cineverse.data.repository.MovieRepositoryImpl
import com.example.cineverse.data.repository.ProfileRepositoryImpl
import com.example.cineverse.domain.repository.MovieRepository
import com.example.cineverse.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Binds domain/repository interfaces to their data/repository implementations. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(impl: MovieRepositoryImpl): MovieRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}

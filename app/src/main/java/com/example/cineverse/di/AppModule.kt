package com.example.cineverse.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * App-wide bindings that don't belong in Network/Database/Repository modules
 * (dispatchers, resource providers, etc.) go here.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule

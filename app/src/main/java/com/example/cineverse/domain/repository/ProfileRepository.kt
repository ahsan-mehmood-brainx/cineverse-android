package com.example.cineverse.domain.repository

import com.example.cineverse.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfile(): Flow<Profile>
    suspend fun saveProfile(profile: Profile)
}

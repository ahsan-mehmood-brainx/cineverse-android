package com.example.cineverse.data.repository

import com.example.cineverse.data.local.dao.ProfileDao
import com.example.cineverse.data.mapper.toDomain
import com.example.cineverse.data.mapper.toEntity
import com.example.cineverse.domain.model.Profile
import com.example.cineverse.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao
) : ProfileRepository {

    override fun observeProfile(): Flow<Profile> =
        profileDao.observe().map { it?.toDomain() ?: Profile.EMPTY }

    override suspend fun saveProfile(profile: Profile) {
        profileDao.upsert(profile.toEntity())
    }
}

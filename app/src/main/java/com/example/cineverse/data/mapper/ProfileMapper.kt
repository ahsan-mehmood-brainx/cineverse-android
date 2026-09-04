package com.example.cineverse.data.mapper

import com.example.cineverse.data.local.entity.ProfileEntity
import com.example.cineverse.domain.model.Profile

fun ProfileEntity.toDomain(): Profile = Profile(displayName = displayName, bio = bio)

fun Profile.toEntity(): ProfileEntity = ProfileEntity(displayName = displayName, bio = bio)

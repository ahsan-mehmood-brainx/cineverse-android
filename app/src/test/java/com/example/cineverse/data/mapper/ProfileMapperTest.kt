package com.example.cineverse.data.mapper

import com.example.cineverse.data.local.entity.ProfileEntity
import com.example.cineverse.domain.model.Profile
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ProfileMapperTest {

    @Test
    fun profileEntity_toDomain_mapsAllFields() {
        val entity = ProfileEntity(
            displayName = "Alex",
            bio = "Loves sci-fi",
            username = "alex",
            email = "alex@example.com",
            profileImageUri = "content://image/1"
        )

        val profile = entity.toDomain()

        assertThat(profile.displayName).isEqualTo("Alex")
        assertThat(profile.bio).isEqualTo("Loves sci-fi")
        assertThat(profile.username).isEqualTo("alex")
        assertThat(profile.email).isEqualTo("alex@example.com")
        assertThat(profile.profileImageUri).isEqualTo("content://image/1")
    }

    @Test
    fun profileEntity_toDomain_nullProfileImageUri_mapsToNull() {
        val entity = ProfileEntity(
            displayName = "Alex",
            bio = "Loves sci-fi",
            username = "alex",
            email = "alex@example.com",
            profileImageUri = null
        )

        val profile = entity.toDomain()

        assertThat(profile.profileImageUri).isNull()
    }

    @Test
    fun profile_toEntity_mapsAllFields() {
        val profile = Profile(
            displayName = "Alex",
            bio = "Loves sci-fi",
            username = "alex",
            email = "alex@example.com",
            profileImageUri = "content://image/1"
        )

        val entity = profile.toEntity()

        assertThat(entity.displayName).isEqualTo("Alex")
        assertThat(entity.bio).isEqualTo("Loves sci-fi")
        assertThat(entity.username).isEqualTo("alex")
        assertThat(entity.email).isEqualTo("alex@example.com")
        assertThat(entity.profileImageUri).isEqualTo("content://image/1")
        assertThat(entity.id).isEqualTo(ProfileEntity.SINGLETON_ID)
    }

    @Test
    fun profile_toEntity_nullProfileImageUri_mapsToNull() {
        val profile = Profile(
            displayName = "Alex",
            bio = "Loves sci-fi",
            username = "alex",
            email = "alex@example.com",
            profileImageUri = null
        )

        val entity = profile.toEntity()

        assertThat(entity.profileImageUri).isNull()
    }

    @Test
    fun profileEntity_toDomain_thenToEntity_roundTripsToOriginalFields() {
        val original = ProfileEntity(
            displayName = "Sam",
            bio = "Bio",
            username = "sam",
            email = "sam@example.com",
            profileImageUri = "content://image/2"
        )

        val roundTripped = original.toDomain().toEntity()

        assertThat(roundTripped).isEqualTo(original)
    }
}

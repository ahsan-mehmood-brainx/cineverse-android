package com.example.cineverse.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ProfileTest {

    @Test
    fun profile_createsProfileWithAllFields() {
        val profile = Profile(
            displayName = "Alex",
            bio = "Loves sci-fi",
            username = "alex",
            email = "alex@example.com",
            profileImageUri = "content://image/1"
        )

        assertThat(profile.displayName).isEqualTo("Alex")
        assertThat(profile.bio).isEqualTo("Loves sci-fi")
        assertThat(profile.username).isEqualTo("alex")
        assertThat(profile.email).isEqualTo("alex@example.com")
        assertThat(profile.profileImageUri).isEqualTo("content://image/1")
    }

    @Test
    fun profile_defaults_areEmptyUsernameEmailAndNullImage() {
        val profile = Profile(displayName = "Alex", bio = "Loves sci-fi")

        assertThat(profile.username).isEmpty()
        assertThat(profile.email).isEmpty()
        assertThat(profile.profileImageUri).isNull()
    }

    @Test
    fun profile_equality() {
        val profile1 = Profile(displayName = "Alex", bio = "Bio", username = "alex", email = "alex@example.com")
        val profile2 = Profile(displayName = "Alex", bio = "Bio", username = "alex", email = "alex@example.com")

        assertThat(profile1).isEqualTo(profile2)
    }

    @Test
    fun profile_inequality_differentDisplayName() {
        val profile1 = Profile(displayName = "Alex", bio = "Bio")
        val profile2 = Profile(displayName = "Sam", bio = "Bio")

        assertThat(profile1).isNotEqualTo(profile2)
    }

    @Test
    fun profile_inequality_differentProfileImageUri() {
        val profile1 = Profile(displayName = "Alex", bio = "Bio", profileImageUri = "content://a")
        val profile2 = Profile(displayName = "Alex", bio = "Bio", profileImageUri = "content://b")

        assertThat(profile1).isNotEqualTo(profile2)
    }

    @Test
    fun profileEmpty_hasAllEmptyFieldsAndNullImage() {
        val empty = Profile.EMPTY

        assertThat(empty.displayName).isEmpty()
        assertThat(empty.bio).isEmpty()
        assertThat(empty.username).isEmpty()
        assertThat(empty.email).isEmpty()
        assertThat(empty.profileImageUri).isNull()
    }
}

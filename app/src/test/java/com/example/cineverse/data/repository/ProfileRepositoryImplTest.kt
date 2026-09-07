package com.example.cineverse.data.repository

import app.cash.turbine.turbineScope
import com.example.cineverse.data.local.dao.ProfileDao
import com.example.cineverse.data.local.entity.ProfileEntity
import com.example.cineverse.domain.model.Profile
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ProfileRepositoryImplTest {

    private val profileDao: ProfileDao = mock()
    private val observedProfile = MutableStateFlow<ProfileEntity?>(null)

    private val repository: ProfileRepositoryImpl by lazy {
        whenever(profileDao.observe()).thenReturn(observedProfile)
        ProfileRepositoryImpl(profileDao)
    }

    @Test
    fun observeProfile_nullEntity_emitsProfileEmpty() = runTest {
        turbineScope {
            val turbine = repository.observeProfile().testIn(this)

            assertThat(turbine.awaitItem()).isEqualTo(Profile.EMPTY)

            turbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeProfile_populatedEntity_emitsMappedProfile() = runTest {
        turbineScope {
            val turbine = repository.observeProfile().testIn(this)
            turbine.awaitItem() // initial null -> EMPTY

            observedProfile.value = ProfileEntity(
                displayName = "Alex",
                bio = "Loves sci-fi",
                username = "alex",
                email = "alex@example.com",
                profileImageUri = "content://image/1"
            )

            val profile = turbine.awaitItem()
            assertThat(profile).isEqualTo(
                Profile(
                    displayName = "Alex",
                    bio = "Loves sci-fi",
                    username = "alex",
                    email = "alex@example.com",
                    profileImageUri = "content://image/1"
                )
            )

            turbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun saveProfile_upsertsMappedEntity() = runTest {
        val profile = Profile(
            displayName = "Alex",
            bio = "Loves sci-fi",
            username = "alex",
            email = "alex@example.com",
            profileImageUri = "content://image/1"
        )

        repository.saveProfile(profile)

        verify(profileDao).upsert(
            ProfileEntity(
                displayName = "Alex",
                bio = "Loves sci-fi",
                username = "alex",
                email = "alex@example.com",
                profileImageUri = "content://image/1"
            )
        )
    }
}

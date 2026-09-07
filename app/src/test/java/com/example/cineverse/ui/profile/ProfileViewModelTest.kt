package com.example.cineverse.ui.profile

import com.example.cineverse.domain.model.Profile
import com.example.cineverse.domain.repository.ProfileRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeProfileRepository : ProfileRepository {
        val state = MutableStateFlow(Profile.EMPTY)
        var saved: Profile? = null

        override fun observeProfile() = state
        override suspend fun saveProfile(profile: Profile) {
            saved = profile
            state.value = profile
        }
    }

    private lateinit var repository: FakeProfileRepository
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeProfileRepository()
        viewModel = ProfileViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun validate_blankName_returnsBlankNameError() {
        assertThat(viewModel.validate("   ", "alex", "alex@example.com", "bio")).isEqualTo(ProfileFormError.BlankName)
    }

    @Test
    fun validate_nameOverLimit_returnsNameTooLongError() {
        val longName = "a".repeat(ProfileViewModel.MAX_NAME_LENGTH + 1)
        assertThat(viewModel.validate(longName, "alex", "alex@example.com", "")).isEqualTo(ProfileFormError.NameTooLong)
    }

    @Test
    fun validate_blankUsername_returnsBlankUsernameError() {
        assertThat(viewModel.validate("Alex", "   ", "alex@example.com", "bio")).isEqualTo(ProfileFormError.BlankUsername)
    }

    @Test
    fun validate_usernameOverLimit_returnsUsernameTooLongError() {
        val longUsername = "a".repeat(ProfileViewModel.MAX_USERNAME_LENGTH + 1)
        assertThat(viewModel.validate("Alex", longUsername, "alex@example.com", "")).isEqualTo(ProfileFormError.UsernameTooLong)
    }

    @Test
    fun validate_blankEmail_returnsBlankEmailError() {
        assertThat(viewModel.validate("Alex", "alex", "   ", "bio")).isEqualTo(ProfileFormError.BlankEmail)
    }

    @Test
    fun validate_invalidEmail_returnsInvalidEmailError() {
        assertThat(viewModel.validate("Alex", "alex", "not-an-email", "bio")).isEqualTo(ProfileFormError.InvalidEmail)
    }

    @Test
    fun validate_bioOverLimit_returnsBioTooLongError() {
        val longBio = "a".repeat(ProfileViewModel.MAX_BIO_LENGTH + 1)
        assertThat(viewModel.validate("Alex", "alex", "alex@example.com", longBio)).isEqualTo(ProfileFormError.BioTooLong)
    }

    @Test
    fun validate_validInput_returnsNull() {
        assertThat(viewModel.validate("Alex", "alex", "alex@example.com", "Loves sci-fi")).isNull()
    }

    @Test
    fun save_validInput_trimsAndPersists() = runTest(testDispatcher) {
        val saved = viewModel.save("  Alex  ", "  alex  ", "  alex@example.com  ", "  Loves sci-fi  ", null)
        advanceUntilIdle()

        assertThat(saved).isTrue()
        assertThat(repository.saved).isEqualTo(
            Profile(displayName = "Alex", bio = "Loves sci-fi", username = "alex", email = "alex@example.com")
        )
    }

    @Test
    fun save_invalidInput_doesNotPersist() = runTest(testDispatcher) {
        val saved = viewModel.save("   ", "alex", "alex@example.com", "bio", null)
        advanceUntilIdle()

        assertThat(saved).isFalse()
        assertThat(repository.saved).isNull()
    }
}

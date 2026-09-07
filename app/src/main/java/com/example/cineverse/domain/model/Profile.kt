package com.example.cineverse.domain.model

/** Local, backend-less user profile. Empty strings mean "not set up yet". */
data class Profile(
    val displayName: String,
    val bio: String,
    val username: String = "",
    val email: String = "",
    val profileImageUri: String? = null
) {
    companion object {
        val EMPTY = Profile(displayName = "", bio = "", username = "", email = "", profileImageUri = null)
    }
}

package com.example.cineverse.domain.model

/** Local, backend-less user profile. Empty strings mean "not set up yet". */
data class Profile(
    val displayName: String,
    val bio: String
) {
    companion object {
        val EMPTY = Profile(displayName = "", bio = "")
    }
}

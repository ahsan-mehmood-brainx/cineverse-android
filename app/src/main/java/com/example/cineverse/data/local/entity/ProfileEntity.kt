package com.example.cineverse.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Single-row table (fixed [id] = 0) holding the local, backend-less user profile. */
@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    val displayName: String,
    val bio: String,
    @ColumnInfo(defaultValue = "") val username: String = "",
    @ColumnInfo(defaultValue = "") val email: String = "",
    val profileImageUri: String? = null
) {
    companion object {
        const val SINGLETON_ID = 0
    }
}

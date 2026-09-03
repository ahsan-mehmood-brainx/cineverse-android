package com.example.cineverse.util

/** Wraps data emitted from the repository layer so the UI can render loading/success/error states. */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}

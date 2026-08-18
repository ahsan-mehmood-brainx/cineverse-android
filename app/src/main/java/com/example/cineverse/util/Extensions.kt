package com.example.cineverse.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/** Java interop example — delegates to the Java utility class in this same package. */
fun String.capitalizeWords(): String = JavaTextUtils.capitalizeWords(this)

/** Collects [this] flow only while the fragment's view is at least STARTED. */
fun <T> Flow<T>.collectOnStarted(fragment: Fragment, action: suspend (T) -> Unit) {
    fragment.viewLifecycleOwner.lifecycleScope.launch {
        fragment.viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            collectLatest(action)
        }
    }
}

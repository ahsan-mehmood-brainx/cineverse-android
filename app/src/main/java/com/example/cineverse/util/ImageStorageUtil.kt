package com.example.cineverse.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.IOException

/**
 * Copies a picked photo's bytes into app-private storage so the profile image survives the
 * originating picker's URI permission being revoked (e.g. after a device reboot).
 */
object ImageStorageUtil {

    private const val FILE_NAME = "profile_image.jpg"

    fun copyToInternalStorage(context: Context, sourceUri: Uri): String? = try {
        val destFile = File(context.filesDir, FILE_NAME)
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            destFile.outputStream().use { output -> input.copyTo(output) }
        }
        Uri.fromFile(destFile).toString()
    } catch (e: IOException) {
        null
    }
}

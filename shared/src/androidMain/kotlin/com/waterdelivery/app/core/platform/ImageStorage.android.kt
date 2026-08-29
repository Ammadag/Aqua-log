package com.waterdelivery.app.core.platform

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

actual class ImageStorage(private val context: Context) {
    actual suspend fun saveImage(uriString: String): String? = withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse(uriString)
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
            val file = File(context.filesDir, "brand_logo.png")
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    actual fun getImagePath(): String? {
        val file = File(context.filesDir, "brand_logo.png")
        return if (file.exists()) file.absolutePath else null
    }
}

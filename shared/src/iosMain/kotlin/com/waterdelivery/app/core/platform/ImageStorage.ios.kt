package com.waterdelivery.app.core.platform

actual class ImageStorage {
    actual suspend fun saveImage(uriString: String): String? {
        // iOS implementation placeholder
        return null
    }

    actual fun getImagePath(): String? {
        return null
    }
}

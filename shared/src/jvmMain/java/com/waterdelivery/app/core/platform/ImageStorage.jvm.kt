package com.waterdelivery.app.core.platform

actual class ImageStorage {
    actual suspend fun saveImage(uriString: String): String? {
        TODO("Not yet implemented")
    }

    actual fun getImagePath(): String? {
        TODO("Not yet implemented")
    }
}
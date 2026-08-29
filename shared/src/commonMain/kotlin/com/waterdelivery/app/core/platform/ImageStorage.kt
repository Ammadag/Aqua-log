package com.waterdelivery.app.core.platform

expect class ImageStorage {
    suspend fun saveImage(uriString: String): String?
    fun getImagePath(): String?
}

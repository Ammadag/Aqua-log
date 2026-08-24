package com.waterdelivery.app.core.platform

actual interface ContactManager {
    actual suspend fun getContacts(): ContactFetchResult
    actual fun hasContactPermission(): Boolean
}

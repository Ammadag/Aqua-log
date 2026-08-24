package com.waterdelivery.app.core.platform

actual interface ContactManager {
    actual suspend fun getContacts(): ContactFetchResult
    actual fun hasContactPermission(): Boolean
}

class IosContactManager : ContactManager {
    override suspend fun getContacts(): ContactFetchResult = ContactFetchResult.Success(emptyList())
    override fun hasContactPermission(): Boolean = false
}

package com.waterdelivery.app.core.platform

import com.waterdelivery.app.presentation.contact_picker.ContactItem

sealed interface ContactFetchResult {
    data class Success(val contacts: List<ContactItem>) : ContactFetchResult
    object PermissionDenied : ContactFetchResult
    data class SecurityError(val message: String) : ContactFetchResult
    data class Error(val message: String) : ContactFetchResult
}

expect interface ContactManager {
    suspend fun getContacts(): ContactFetchResult
    fun hasContactPermission(): Boolean
}

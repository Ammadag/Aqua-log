package com.waterdelivery.app.core.platform

import com.waterdelivery.app.presentation.contact_picker.ContactItem

actual interface ContactManager {
    actual suspend fun getContacts(): List<ContactItem>
    actual fun hasContactPermission(): Boolean
}
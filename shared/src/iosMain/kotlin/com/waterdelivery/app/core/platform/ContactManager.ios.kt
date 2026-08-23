package com.waterdelivery.app.core.platform

import com.waterdelivery.app.presentation.contact_picker.ContactItem

actual interface ContactManager {
    actual suspend fun getContacts(): List<ContactItem>
    actual fun hasContactPermission(): Boolean
}

class IosContactManager : ContactManager {
    override suspend fun getContacts(): List<ContactItem> = emptyList()
    override fun hasContactPermission(): Boolean = false
}

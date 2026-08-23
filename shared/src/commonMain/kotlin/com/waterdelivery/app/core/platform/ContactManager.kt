package com.waterdelivery.app.core.platform

import com.waterdelivery.app.presentation.contact_picker.ContactItem

expect interface ContactManager {
    suspend fun getContacts(): List<ContactItem>
    fun hasContactPermission(): Boolean
}

package com.waterdelivery.app.core.platform

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import com.waterdelivery.app.presentation.contact_picker.ContactItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual interface ContactManager {
    actual suspend fun getContacts(): List<ContactItem>
    actual fun hasContactPermission(): Boolean
}

class AndroidContactManager(private val context: Context) : ContactManager {

    override fun hasContactPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun getContacts(): List<ContactItem> = withContext(Dispatchers.IO) {
        val contactList = mutableListOf<ContactItem>()
        val contentResolver = context.contentResolver
        
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex) ?: ""
                val number = it.getString(numberIndex) ?: ""
                val id = it.getString(idIndex) ?: ""
                
                // Avoid duplicate contacts for same ID/number if any
                if (contactList.none { c -> c.phoneNumber == number }) {
                    contactList.add(ContactItem(id, name, number))
                }
            }
        }
        contactList
    }
}

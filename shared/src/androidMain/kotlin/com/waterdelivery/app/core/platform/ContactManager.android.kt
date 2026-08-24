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
    actual suspend fun getContacts(): ContactFetchResult
    actual fun hasContactPermission(): Boolean
}

class AndroidContactManager(private val context: Context) : ContactManager {

    override fun hasContactPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun getContacts(): ContactFetchResult = withContext(Dispatchers.IO) {
        if (!hasContactPermission()) {
            return@withContext ContactFetchResult.PermissionDenied
        }

        val contactList = mutableListOf<ContactItem>()
        val seenNumbers = HashSet<String>()

        try {
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            )

            if (cursor == null) {
                return@withContext ContactFetchResult.Error("Unable to access contact content provider.")
            }

            cursor.use { c ->
                val idIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                if (idIndex < 0 || nameIndex < 0 || numberIndex < 0) {
                    return@withContext ContactFetchResult.Error("Contact details format incompatible on this device.")
                }

                while (c.moveToNext()) {
                    val id = c.getString(idIndex) ?: continue
                    val name = c.getString(nameIndex) ?: ""
                    val number = c.getString(numberIndex) ?: continue

                    if (seenNumbers.add(number)) {
                        contactList.add(ContactItem(id, name, number))
                    }
                }
            }

            ContactFetchResult.Success(contactList)
        } catch (e: SecurityException) {
            ContactFetchResult.SecurityError(
                e.localizedMessage ?: "Security Exception: Contact permission was revoked or restricted by system."
            )
        } catch (e: IllegalArgumentException) {
            ContactFetchResult.Error(
                e.localizedMessage ?: "Invalid query parameter encountered while fetching contacts."
            )
        } catch (e: IllegalStateException) {
            ContactFetchResult.Error(
                e.localizedMessage ?: "Contacts provider database state error."
            )
        } catch (e: Throwable) {
            ContactFetchResult.Error(
                e.localizedMessage ?: "An unexpected error occurred while loading contacts."
            )
        }
    }
}

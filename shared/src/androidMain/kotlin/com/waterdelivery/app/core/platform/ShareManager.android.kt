package com.waterdelivery.app.core.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

actual interface ShareManager {
    actual fun shareFile(filePath: String, mimeType: String)
    actual fun shareFileViaWhatsApp(filePath: String, phoneNumber: String)
    actual fun makeCall(phoneNumber: String)
    actual fun openWhatsApp(phoneNumber: String, message: String)
}

class AndroidShareManager(private val context: Context) : ShareManager {
    override fun shareFile(filePath: String, mimeType: String) {
        val file = File(filePath)
        if (!file.exists()) return

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Share Invoice")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    override fun shareFileViaWhatsApp(filePath: String, phoneNumber: String) {
        val file = File(filePath)
        if (!file.exists()) return

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        // Sanitize phone number: remove any non-digit characters
        val cleanNumber = phoneNumber.filter { it.isDigit() }
        val jid = "$cleanNumber@s.whatsapp.net"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra("jid", jid)
            setPackage("com.whatsapp")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to regular sharing if WhatsApp is not installed
            shareFile(filePath, "application/pdf")
        }
    }

    override fun makeCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    override fun openWhatsApp(phoneNumber: String, message: String) {
        try {
            val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // WhatsApp not installed or other error
        }
    }
}

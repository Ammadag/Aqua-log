package com.waterdelivery.app.core.platform

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
        if (!file.exists()) {
            Toast.makeText(context, "Error: File not found", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            clipData = ClipData.newRawUri("", uri)
        }

        val chooser = Intent.createChooser(intent, "Share Invoice")
        // Apply flags and clipData to the chooser intent as well for Android 10+
        chooser.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        chooser.clipData = ClipData.newRawUri("", uri)
        
        if (context !is android.app.Activity) {
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        try {
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to open share menu", Toast.LENGTH_SHORT).show()
        }
    }

    override fun shareFileViaWhatsApp(filePath: String, phoneNumber: String) {

        val file = File(filePath)
        if (!file.exists()) {
            return
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            clipData = ClipData.newRawUri("", uri)
        }
        if (context !is android.app.Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // Try regular WhatsApp first
        intent.setPackage("com.whatsapp")
        try {
            context.startActivity(intent)
            return
        } catch (e: Exception) {
            // Try WhatsApp Business
            intent.setPackage("com.whatsapp.w4b")
            try {
                context.startActivity(intent)
                return
            } catch (e2: Exception) {
                // Fallback to regular sharing if neither is installed
                shareFile(filePath, "application/pdf")
            }
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
            // Format number for the URL as well
            var cleanNumber = phoneNumber.filter { it.isDigit() }
            if (cleanNumber.startsWith("03") && cleanNumber.length == 11) {
                cleanNumber = "92" + cleanNumber.substring(1)
            }
            
            val url = "https://api.whatsapp.com/send?phone=$cleanNumber&text=${Uri.encode(message)}"
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

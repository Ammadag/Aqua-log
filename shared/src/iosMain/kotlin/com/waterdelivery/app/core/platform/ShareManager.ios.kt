package com.waterdelivery.app.core.platform

import platform.Foundation.NSURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual interface ShareManager {
    actual fun shareFile(filePath: String, mimeType: String)
    actual fun shareFileViaWhatsApp(filePath: String, phoneNumber: String)
    actual fun makeCall(phoneNumber: String)
    actual fun openWhatsApp(phoneNumber: String, message: String)
}

class IosShareManager : ShareManager {
    override fun shareFile(filePath: String, mimeType: String) {
        val url = NSURL.fileURLWithPath(filePath)
        val activityController = UIActivityViewController(listOf(url), null)

        val window = UIApplication.sharedApplication.keyWindow
        window?.rootViewController?.presentViewController(activityController, true, null)
    }

    override fun shareFileViaWhatsApp(filePath: String, phoneNumber: String) {
        // Targeted file sharing to a specific contact is not directly supported via URL schemes on iOS
        // for files. We fallback to general sharing.
        shareFile(filePath, "application/pdf")
    }

    override fun makeCall(phoneNumber: String) {
        val url = NSURL.URLWithString("tel:$phoneNumber")
        if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }

    override fun openWhatsApp(phoneNumber: String, message: String) {
        var cleanNumber = phoneNumber.filter { it.isDigit() }
        if (cleanNumber.startsWith("03") && cleanNumber.length == 11) {
            cleanNumber = "92" + cleanNumber.substring(1)
        }
        
        val encodedMessage = message.replace(" ", "%20")
        val urlString = "whatsapp://send?phone=$cleanNumber&text=$encodedMessage"
        val url = NSURL.URLWithString(urlString)
        if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }
}

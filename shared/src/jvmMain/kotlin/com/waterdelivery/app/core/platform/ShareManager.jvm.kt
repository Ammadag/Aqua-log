package com.waterdelivery.app.core.platform

actual interface ShareManager {
    actual fun shareFile(filePath: String, mimeType: String)
    actual fun shareFileViaWhatsApp(filePath: String, phoneNumber: String)
    actual fun makeCall(phoneNumber: String)
    actual fun openWhatsApp(phoneNumber: String, message: String)
}
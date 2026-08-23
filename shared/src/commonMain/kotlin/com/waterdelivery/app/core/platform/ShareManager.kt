package com.waterdelivery.app.core.platform

expect interface ShareManager {
    fun shareFile(filePath: String, mimeType: String = "application/pdf")
    fun shareFileViaWhatsApp(filePath: String, phoneNumber: String)
    fun makeCall(phoneNumber: String)
    fun openWhatsApp(phoneNumber: String, message: String = "")
}

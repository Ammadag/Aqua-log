package com.waterdelivery.app.domain.model

data class BusinessProfile(
    val id: Int = 1,
    val businessName: String,
    val phone: String,
    val address: String,
    val defaultPricePerBottle: Double,
    val invoicePrefix: String = "INV",
    val logoPath: String? = null,
    val isOnboardingCompleted: Boolean = false
) {
    val isConfigured: Boolean
        get() = businessName.isNotBlank() && phone.isNotBlank()
}

package com.waterdelivery.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_profile")
data class BusinessProfileEntity(
    @PrimaryKey val id: Int = 1,
    val businessName: String,
    val phone: String,
    val address: String,
    val defaultPricePerBottle: Double,
    val invoicePrefix: String,
    val logoPath: String? = null,
    val isOnboardingCompleted: Boolean = false
)

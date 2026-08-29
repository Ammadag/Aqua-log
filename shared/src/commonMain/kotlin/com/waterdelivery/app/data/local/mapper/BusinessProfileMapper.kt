package com.waterdelivery.app.data.local.mapper

import com.waterdelivery.app.data.local.entity.BusinessProfileEntity
import com.waterdelivery.app.domain.model.BusinessProfile

fun BusinessProfileEntity.toDomain(): BusinessProfile = BusinessProfile(
    id = id,
    businessName = businessName,
    phone = phone,
    address = address,
    defaultPricePerBottle = defaultPricePerBottle,
    invoicePrefix = invoicePrefix,
    logoPath = logoPath,
    isOnboardingCompleted = isOnboardingCompleted
)

fun BusinessProfile.toEntity(): BusinessProfileEntity = BusinessProfileEntity(
    id = id,
    businessName = businessName,
    phone = phone,
    address = address,
    defaultPricePerBottle = defaultPricePerBottle,
    invoicePrefix = invoicePrefix,
    logoPath = logoPath,
    isOnboardingCompleted = isOnboardingCompleted
)

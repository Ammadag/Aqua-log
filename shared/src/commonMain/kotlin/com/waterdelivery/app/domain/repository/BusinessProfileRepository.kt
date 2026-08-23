package com.waterdelivery.app.domain.repository

import com.waterdelivery.app.domain.model.BusinessProfile
import kotlinx.coroutines.flow.Flow

interface BusinessProfileRepository {
    fun getBusinessProfile(): Flow<BusinessProfile?>
    suspend fun saveBusinessProfile(profile: BusinessProfile)
}

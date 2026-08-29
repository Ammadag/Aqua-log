package com.waterdelivery.app.data.repository

import com.waterdelivery.app.data.local.dao.BusinessProfileDao
import com.waterdelivery.app.data.local.mapper.toDomain
import com.waterdelivery.app.data.local.mapper.toEntity
import com.waterdelivery.app.domain.model.BusinessProfile
import com.waterdelivery.app.domain.repository.BusinessProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class BusinessProfileRepositoryImpl(
    private val businessProfileDao: BusinessProfileDao
) : BusinessProfileRepository {

    override fun getBusinessProfile(): Flow<BusinessProfile?> {
        return businessProfileDao.getBusinessProfile().map { it?.toDomain() }
    }

    override suspend fun saveBusinessProfile(profile: BusinessProfile) {
        businessProfileDao.insertOrUpdateProfile(profile.toEntity())
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        val currentProfile = businessProfileDao.getBusinessProfile().first()?.toDomain()
        val updatedProfile = if (currentProfile != null) {
            currentProfile.copy(isOnboardingCompleted = completed)
        } else {
            BusinessProfile(
                businessName = "",
                phone = "",
                address = "",
                defaultPricePerBottle = 80.0,
                isOnboardingCompleted = completed
            )
        }
        businessProfileDao.insertOrUpdateProfile(updatedProfile.toEntity())
    }
}

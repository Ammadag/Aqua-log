package com.waterdelivery.app.domain.usecase

import com.waterdelivery.app.domain.model.Delivery
import com.waterdelivery.app.domain.repository.BusinessProfileRepository
import com.waterdelivery.app.domain.repository.DeliveryRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlin.time.Clock

class RecordDeliveryUseCase(
    private val deliveryRepository: DeliveryRepository,
    private val businessProfileRepository: BusinessProfileRepository
) {
    suspend operator fun invoke(
        customerId: String,
        date: Long,
        deliveredQty: Int,
        returnedQty: Int,
        customPrice: Double? = null,
        notes: String? = null
    ): Result<Unit> {
        if (deliveredQty < 0 || returnedQty < 0) {
            return Result.failure(IllegalArgumentException("Quantities cannot be negative"))
        }
        if (deliveredQty == 0 && returnedQty == 0) {
            return Result.failure(IllegalArgumentException("At least one bottle must be delivered or returned"))
        }

        val price = if (customPrice != null && customPrice > 0.0) {
            customPrice
        } else {
            val profile = businessProfileRepository.getBusinessProfile().firstOrNull()
            profile?.defaultPricePerBottle ?: 0.0
        }

        val now = Clock.System.now().toEpochMilliseconds()
        val delivery = Delivery(
            id = now.toString(),
            customerId = customerId,
            date = date,
            deliveredQuantity = deliveredQty,
            returnedQuantity = returnedQty,
            pricePerBottle = price,
            notes = notes,
            createdAt = now
        )

        return try {
            deliveryRepository.addDelivery(delivery)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

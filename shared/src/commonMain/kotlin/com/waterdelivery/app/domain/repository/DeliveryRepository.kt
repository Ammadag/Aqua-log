package com.waterdelivery.app.domain.repository

import com.waterdelivery.app.domain.model.Delivery
import kotlinx.coroutines.flow.Flow

interface DeliveryRepository {
    suspend fun addDelivery(delivery: Delivery)
    suspend fun deleteDelivery(delivery: Delivery)
    fun getDeliveriesForCustomer(customerId: String): Flow<List<Delivery>>
    suspend fun getDeliveriesForCustomerInRange(
        customerId: String,
        startDate: Long,
        endDate: Long
    ): List<Delivery>
    fun getTodayDeliveries(): Flow<List<Delivery>>
}

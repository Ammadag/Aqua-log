package com.waterdelivery.app.data.repository

import com.waterdelivery.app.data.local.dao.DeliveryDao
import com.waterdelivery.app.data.local.mapper.toDomain
import com.waterdelivery.app.data.local.mapper.toEntity
import com.waterdelivery.app.domain.model.Delivery
import com.waterdelivery.app.domain.repository.DeliveryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.atTime

class DeliveryRepositoryImpl(
    private val deliveryDao: DeliveryDao
) : DeliveryRepository {

    override suspend fun addDelivery(delivery: Delivery) {
        deliveryDao.insertDelivery(delivery.toEntity())
    }

    override suspend fun deleteDelivery(delivery: Delivery) {
        deliveryDao.deleteDelivery(delivery.toEntity())
    }

    override fun getDeliveriesForCustomer(customerId: String): Flow<List<Delivery>> {
        return deliveryDao.getDeliveriesForCustomer(customerId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getDeliveriesForCustomerInRange(
        customerId: String,
        startDate: Long,
        endDate: Long
    ): List<Delivery> {
        return deliveryDao.getDeliveriesForCustomerInRange(customerId, startDate, endDate)
            .map { it.toDomain() }
    }

    override fun getTodayDeliveries(): Flow<List<Delivery>> {
        val now = Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds())
        val tz = TimeZone.currentSystemDefault()
        val today = now.toLocalDateTime(tz).date
        
        val startOfDay = today.atTime(0, 0, 0, 0).toInstant(tz).toEpochMilliseconds()
        val endOfDay = today.atTime(23, 59, 59, 999999999).toInstant(tz).toEpochMilliseconds()
        
        return deliveryDao.getTodayDeliveries(startOfDay, endOfDay).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}

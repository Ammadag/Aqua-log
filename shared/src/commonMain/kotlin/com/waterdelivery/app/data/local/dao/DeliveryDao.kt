package com.waterdelivery.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.waterdelivery.app.data.local.entity.DeliveryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeliveryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDelivery(delivery: DeliveryEntity)

    @Delete
    suspend fun deleteDelivery(delivery: DeliveryEntity)

    @Query("SELECT * FROM deliveries WHERE customerId = :customerId ORDER BY date DESC")
    fun getDeliveriesForCustomer(customerId: String): Flow<List<DeliveryEntity>>

    @Query(
        "SELECT * FROM deliveries WHERE customerId = :customerId AND date BETWEEN :startDate AND :endDate ORDER BY date ASC",
    )
    suspend fun getDeliveriesForCustomerInRange(
        customerId: String,
        startDate: Long,
        endDate: Long,
    ): List<DeliveryEntity>

    @Query("SELECT * FROM deliveries WHERE date BETWEEN :startOfDay AND :endOfDay")
    fun getTodayDeliveries(startOfDay: Long, endOfDay: Long): Flow<List<DeliveryEntity>>
}

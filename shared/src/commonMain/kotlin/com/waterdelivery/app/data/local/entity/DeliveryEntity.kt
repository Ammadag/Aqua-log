package com.waterdelivery.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "deliveries",
    indices = [Index("customerId")],
)
data class DeliveryEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val date: Long,
    val deliveredQuantity: Int,
    val returnedQuantity: Int,
    val pricePerBottle: Double,
    val totalAmount: Double,
    val notes: String?,
    val createdAt: Long,
)

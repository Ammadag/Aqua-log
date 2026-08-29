package com.waterdelivery.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "invoices",
    indices = [
        Index("customerId"),
        Index(value = ["invoiceNumber"], unique = true),
    ],
)
data class InvoiceEntity(
    @PrimaryKey val id: String,
    val invoiceNumber: String,
    val customerId: String,
    val startDate: Long,
    val endDate: Long,
    val totalDelivered: Int,
    val totalReturned: Int,
    val totalAmount: Double,
    val createdAt: Long,
    val isPinned: Boolean = false,
)

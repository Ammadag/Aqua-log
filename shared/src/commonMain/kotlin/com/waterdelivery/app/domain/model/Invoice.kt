package com.waterdelivery.app.domain.model

data class Invoice(
    val id: String,
    val invoiceNumber: String,
    val customerId: String,
    val startDate: Long,
    val endDate: Long,
    val totalDelivered: Int,
    val totalReturned: Int,
    val totalAmount: Double,
    val createdAt: Long,
)

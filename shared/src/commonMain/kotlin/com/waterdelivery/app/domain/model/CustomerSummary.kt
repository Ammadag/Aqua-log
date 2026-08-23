package com.waterdelivery.app.domain.model

data class CustomerSummary(
    val customer: Customer,
    val totalDeliveredThisMonth: Int,
    val totalReturnedThisMonth: Int,
    val currentBottleBalance: Int,
    val totalAmountThisMonth: Double
)

package com.waterdelivery.app.domain.model

data class DashboardStats(
    val bottlesDeliveredToday: Int,
    val bottlesReturnedToday: Int,
    val totalAmountToday: Double,
    val totalActiveCustomers: Int
)

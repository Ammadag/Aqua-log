package com.waterdelivery.app.presentation.dashboard

import com.waterdelivery.app.domain.model.Customer
import com.waterdelivery.app.domain.model.DashboardStats

data class DashboardUiState(
    val greeting: String = "Good Morning, Delivery Pro",
    val stats: DashboardStats = DashboardStats(0, 0, 0.0, 0),
    val recentCustomers: List<Customer> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

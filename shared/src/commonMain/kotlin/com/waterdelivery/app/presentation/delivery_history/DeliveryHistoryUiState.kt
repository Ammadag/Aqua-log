package com.waterdelivery.app.presentation.delivery_history

import com.waterdelivery.app.domain.model.Delivery

sealed class DateFilter {
    object Weekly : DateFilter()
    object Monthly : DateFilter()
    data class Custom(val startDate: Long, val endDate: Long) : DateFilter()
}

data class DeliveryHistoryUiState(
    val customerName: String = "",
    val deliveries: List<Delivery> = emptyList(),
    val isLoading: Boolean = false,
    val dateFilter: DateFilter = DateFilter.Monthly,
    val errorMessage: String? = null
)

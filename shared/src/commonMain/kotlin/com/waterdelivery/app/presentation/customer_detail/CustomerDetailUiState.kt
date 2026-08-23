package com.waterdelivery.app.presentation.customer_detail

import com.waterdelivery.app.domain.model.CustomerSummary
import com.waterdelivery.app.domain.model.Delivery

data class CustomerDetailUiState(
    val summary: CustomerSummary? = null,
    val deliveryHistory: List<Delivery> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

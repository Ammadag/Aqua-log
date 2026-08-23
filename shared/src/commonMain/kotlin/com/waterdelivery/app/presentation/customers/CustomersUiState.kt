package com.waterdelivery.app.presentation.customers

import com.waterdelivery.app.domain.model.Customer

data class CustomersUiState(
    val searchQuery: String = "",
    val customers: List<Customer> = emptyList(),
    val isLoading: Boolean = false
)

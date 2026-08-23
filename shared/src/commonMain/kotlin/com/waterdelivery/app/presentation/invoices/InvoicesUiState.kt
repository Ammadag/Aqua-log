package com.waterdelivery.app.presentation.invoices

import com.waterdelivery.app.domain.model.Invoice

data class InvoicesUiState(
    val invoices: List<Invoice> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

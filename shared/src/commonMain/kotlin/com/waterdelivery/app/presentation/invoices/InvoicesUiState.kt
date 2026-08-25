package com.waterdelivery.app.presentation.invoices

import com.waterdelivery.app.domain.model.Invoice

data class InvoiceItemUiModel(
    val invoice: Invoice,
    val customerName: String
)

data class InvoicesUiState(
    val invoices: List<InvoiceItemUiModel> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

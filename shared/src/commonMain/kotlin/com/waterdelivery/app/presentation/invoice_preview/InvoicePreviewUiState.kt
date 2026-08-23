package com.waterdelivery.app.presentation.invoice_preview

import com.waterdelivery.app.domain.model.BusinessProfile
import com.waterdelivery.app.domain.model.Customer
import com.waterdelivery.app.domain.model.Delivery
import com.waterdelivery.app.domain.model.Invoice

data class InvoicePreviewUiState(
    val invoice: Invoice? = null,
    val customer: Customer? = null,
    val businessProfile: BusinessProfile? = null,
    val deliveries: List<Delivery> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

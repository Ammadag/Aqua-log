package com.waterdelivery.app.presentation.add_delivery

import com.waterdelivery.app.domain.model.Customer
import kotlin.time.Clock

data class AddDeliveryUiState(
    val selectedCustomerId: String = "",
    val selectedCustomerName: String = "",
    val selectedCustomerPhone: String = "",
    val selectedCustomerAddress: String = "",
    val customers: List<Customer> = emptyList(),
    val deliveredQuantity: Int = 1,
    val returnedQuantity: Int = 0,
    val pricePerBottle: String = "80",
    val date: Long = Clock.System.now().toEpochMilliseconds(),
    val notes: String = "",
    val computedTotal: Double = 80.0,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

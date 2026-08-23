package com.waterdelivery.app.presentation.add_customer

data class AddCustomerUiState(
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

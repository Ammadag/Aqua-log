package com.waterdelivery.app.presentation.settings

data class SettingsUiState(
    val businessName: String = "",
    val phone: String = "",
    val address: String = "",
    val defaultPrice: String = "80",
    val invoicePrefix: String = "INV",
    val isSaving: Boolean = false,
    val isSavedSuccess: Boolean = false,
    val appVersion: String = "1.0.0",
    val errorMessage: String? = null,
    val isLoading: Boolean = true
)

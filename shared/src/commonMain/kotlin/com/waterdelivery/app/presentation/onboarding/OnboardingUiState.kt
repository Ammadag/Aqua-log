package com.waterdelivery.app.presentation.onboarding

data class OnboardingUiState(
    val businessName: String = "",
    val phone: String = "",
    val address: String = "",
    val defaultPrice: String = "80",
    val logoPath: String? = null,
    val isSaving: Boolean = false,
    val isCompleted: Boolean = false,
    val errorMessage: String? = null
)

package com.waterdelivery.app.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterdelivery.app.core.platform.ImageStorage
import com.waterdelivery.app.domain.model.BusinessProfile
import com.waterdelivery.app.domain.repository.BusinessProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val repository: BusinessProfileRepository,
    private val imageStorage: ImageStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onBusinessNameChange(name: String) { _uiState.update { it.copy(businessName = name) } }
    fun onPhoneChange(phone: String) { _uiState.update { it.copy(phone = phone) } }
    fun onAddressChange(address: String) { _uiState.update { it.copy(address = address) } }
    fun onPriceChange(price: String) { _uiState.update { it.copy(defaultPrice = price) } }

    fun onLogoSelected(uriString: String) {
        viewModelScope.launch {
            val path = imageStorage.saveImage(uriString)
            if (path != null) {
                _uiState.update { it.copy(logoPath = path) }
            }
        }
    }

    fun onSaveAndContinue() {
        val state = _uiState.value
        if (state.businessName.isBlank() || state.phone.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Business name and phone are required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                val profile = BusinessProfile(
                    businessName = state.businessName,
                    phone = state.phone,
                    address = state.address,
                    defaultPricePerBottle = state.defaultPrice.toDoubleOrNull() ?: 80.0,
                    logoPath = state.logoPath,
                    isOnboardingCompleted = true
                )
                repository.saveBusinessProfile(profile)
                _uiState.update { it.copy(isSaving = false, isCompleted = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }

    fun onSkipForNow() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                repository.setOnboardingCompleted(true)
                _uiState.update { it.copy(isSaving = false, isCompleted = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }
}

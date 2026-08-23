package com.waterdelivery.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterdelivery.app.domain.model.BusinessProfile
import com.waterdelivery.app.domain.repository.BusinessProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val profileRepository: BusinessProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        profileRepository.getBusinessProfile()
            .onEach { profile ->
                if (profile != null) {
                    _uiState.update { 
                        it.copy(
                            businessName = profile.businessName,
                            phone = profile.phone,
                            address = profile.address,
                            defaultPrice = profile.defaultPricePerBottle.toInt().toString(),
                            invoicePrefix = profile.invoicePrefix,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
            .launchIn(viewModelScope)
    }

    fun onBusinessNameChange(name: String) { _uiState.update { it.copy(businessName = name) } }
    fun onPhoneChange(phone: String) { _uiState.update { it.copy(phone = phone) } }
    fun onAddressChange(address: String) { _uiState.update { it.copy(address = address) } }
    fun onPriceChange(price: String) { _uiState.update { it.copy(defaultPrice = price) } }
    fun onPrefixChange(prefix: String) { _uiState.update { it.copy(invoicePrefix = prefix) } }

    fun saveSettings() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, isSavedSuccess = false) }
            try {
                val profile = BusinessProfile(
                    businessName = state.businessName,
                    phone = state.phone,
                    address = state.address,
                    defaultPricePerBottle = state.defaultPrice.toDoubleOrNull() ?: 0.0,
                    invoicePrefix = state.invoicePrefix
                )
                profileRepository.saveBusinessProfile(profile)
                _uiState.update { it.copy(isSaving = false, isSavedSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }
}

package com.waterdelivery.app.presentation.add_customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterdelivery.app.domain.usecase.AddCustomerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddCustomerViewModel(
    private val addCustomerUseCase: AddCustomerUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddCustomerUiState())
    val uiState: StateFlow<AddCustomerUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onPhoneChange(phone: String) {
        _uiState.update { it.copy(phone = phone) }
    }

    fun onAddressChange(address: String) {
        _uiState.update { it.copy(address = address) }
    }

    fun onNotesChange(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun prefillFromContact(name: String, phone: String) {
        _uiState.update { it.copy(name = name, phone = phone) }
    }

    fun saveCustomer() {
        val currentState = _uiState.value
        if (currentState.name.isBlank() || currentState.phone.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Name and Phone are required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = addCustomerUseCase(
                name = currentState.name,
                phone = currentState.phone,
                address = currentState.address,
                notes = currentState.notes
            )
            
            result.onSuccess {
                _uiState.update { it.copy(isSaving = false, isSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }
}

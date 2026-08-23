package com.waterdelivery.app.presentation.add_delivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterdelivery.app.domain.repository.BusinessProfileRepository
import com.waterdelivery.app.domain.repository.CustomerRepository
import com.waterdelivery.app.domain.usecase.RecordDeliveryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddDeliveryViewModel(
    private val recordDeliveryUseCase: RecordDeliveryUseCase,
    private val customerRepository: CustomerRepository,
    private val businessProfileRepository: BusinessProfileRepository,
    preselectedCustomerId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddDeliveryUiState())
    val uiState: StateFlow<AddDeliveryUiState> = _uiState.asStateFlow()

    init {
        loadInitialData(preselectedCustomerId)
    }

    private fun loadInitialData(customerId: String?) {
        viewModelScope.launch {
            // Load Customers
            customerRepository.getAllCustomers().collect { customers ->
                _uiState.update { it.copy(customers = customers) }
                
                if (customerId != null) {
                    val customer = customers.find { it.id == customerId }
                    customer?.let { selectCustomer(it.id, it.name, it.phoneNumber, it.address) }
                } else if (customers.isNotEmpty() && _uiState.value.selectedCustomerId.isEmpty()) {
                    val first = customers.first()
                    selectCustomer(first.id, first.name, first.phoneNumber, first.address)
                }
            }
        }

        viewModelScope.launch {
            // Load Default Price
            val profile = businessProfileRepository.getBusinessProfile().firstOrNull()
            profile?.let { p ->
                _uiState.update { 
                    it.copy(
                        pricePerBottle = p.defaultPricePerBottle.toInt().toString()
                    )
                }
                updateTotal()
            }
        }
    }

    fun selectCustomer(id: String, name: String, phone: String, address: String) {
        _uiState.update { 
            it.copy(
                selectedCustomerId = id, 
                selectedCustomerName = name,
                selectedCustomerPhone = phone,
                selectedCustomerAddress = address
            ) 
        }
    }

    fun onDeliveredQtyChange(qty: Int) {
        _uiState.update { it.copy(deliveredQuantity = qty) }
        updateTotal()
    }

    fun onReturnedQtyChange(qty: Int) {
        _uiState.update { it.copy(returnedQuantity = qty) }
    }

    fun onPriceChange(price: String) {
        _uiState.update { it.copy(pricePerBottle = price) }
        updateTotal()
    }

    fun onNotesChange(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun onDateChange(date: Long) {
        _uiState.update { it.copy(date = date) }
    }

    private fun updateTotal() {
        val price = _uiState.value.pricePerBottle.toDoubleOrNull() ?: 0.0
        val total = _uiState.value.deliveredQuantity * price
        _uiState.update { it.copy(computedTotal = total) }
    }

    fun saveDelivery() {
        val state = _uiState.value
        if (state.selectedCustomerId.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please select a customer") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val price = state.pricePerBottle.toDoubleOrNull() ?: 0.0
            
            val result = recordDeliveryUseCase(
                customerId = state.selectedCustomerId,
                date = state.date,
                deliveredQty = state.deliveredQuantity,
                returnedQty = state.returnedQuantity,
                customPrice = price,
                notes = state.notes
            )

            result.onSuccess {
                _uiState.update { it.copy(isSaving = false, isSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }
}

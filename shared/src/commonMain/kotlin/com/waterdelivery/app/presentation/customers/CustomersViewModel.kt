package com.waterdelivery.app.presentation.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterdelivery.app.domain.repository.CustomerRepository
import com.waterdelivery.app.domain.usecase.SearchCustomersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.ExperimentalCoroutinesApi

class CustomersViewModel(
    private val searchCustomersUseCase: SearchCustomersUseCase,
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<CustomersUiState> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                customerRepository.getAllCustomers()
            } else {
                searchCustomersUseCase(query)
            }
        }
        .onStart { /* Optionally set loading */ }
        .combine(_searchQuery) { customers, query ->
            CustomersUiState(
                searchQuery = query,
                customers = customers,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CustomersUiState(isLoading = true)
        )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.update { newQuery }
    }
}

package com.waterdelivery.app.presentation.invoices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterdelivery.app.domain.repository.CustomerRepository
import com.waterdelivery.app.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class InvoicesViewModel(
    private val invoiceRepository: InvoiceRepository,
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    
    val uiState: StateFlow<InvoicesUiState> = combine(
        invoiceRepository.getAllInvoices(),
        customerRepository.getAllCustomers(),
        _searchQuery
    ) { invoices, customers, query ->
        val customerMap = customers.associateBy { it.id }
        
        val items = invoices.map { invoice ->
            InvoiceItemUiModel(
                invoice = invoice,
                customerName = customerMap[invoice.customerId]?.name ?: "Unknown Customer"
            )
        }

        val filtered = if (query.isBlank()) items 
                       else items.filter { 
                           it.invoice.invoiceNumber.contains(query, ignoreCase = true) || 
                           it.customerName.contains(query, ignoreCase = true) 
                       }
        
        InvoicesUiState(
            invoices = filtered,
            searchQuery = query,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InvoicesUiState(isLoading = true)
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.update { query }
    }
}

package com.waterdelivery.app.presentation.invoices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterdelivery.app.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class InvoicesViewModel(
    private val invoiceRepository: InvoiceRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    
    val uiState: StateFlow<InvoicesUiState> = combine(
        invoiceRepository.getAllInvoices(),
        _searchQuery
    ) { invoices, query ->
        val filtered = if (query.isBlank()) invoices 
                       else invoices.filter { it.invoiceNumber.contains(query, ignoreCase = true) || it.customerId.contains(query) }
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

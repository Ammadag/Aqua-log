package com.waterdelivery.app.presentation.invoices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterdelivery.app.domain.repository.CustomerRepository
import com.waterdelivery.app.domain.repository.InvoiceRepository
import com.waterdelivery.app.domain.usecase.DeleteInvoiceUseCase
import com.waterdelivery.app.domain.usecase.TogglePinInvoiceUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.toLocalDateTime

class InvoicesViewModel(
    private val invoiceRepository: InvoiceRepository,
    private val customerRepository: CustomerRepository,
    private val deleteInvoiceUseCase: DeleteInvoiceUseCase,
    private val togglePinInvoiceUseCase: TogglePinInvoiceUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow(InvoiceFilter.ALL)

    val uiState: StateFlow<InvoicesUiState> = combine(
        invoiceRepository.getAllInvoices(),
        customerRepository.getAllCustomers(),
        _searchQuery,
        _selectedFilter
    ) { invoices, customers, query, filter ->
        val customerMap = customers.associateBy { it.id }
        val now = kotlinx.datetime.Clock.System.now()
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())

        val items = invoices.map { invoice ->
            InvoiceItemUiModel(
                invoice = invoice,
                customerName = customerMap[invoice.customerId]?.name ?: "Unknown Customer"
            )
        }

        val filteredByDate = when (filter) {
            InvoiceFilter.ALL -> items
            InvoiceFilter.THIS_MONTH -> items.filter {
                val date = kotlinx.datetime.Instant.fromEpochMilliseconds(it.invoice.createdAt)
                    .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                date.month == now.month && date.year == now.year
            }

            InvoiceFilter.LAST_MONTH -> items.filter {
                val date = kotlinx.datetime.Instant.fromEpochMilliseconds(it.invoice.createdAt)
                    .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                val lastMonth = if (now.monthNumber == 1) 12 else now.monthNumber - 1
                val lastMonthYear = if (now.monthNumber == 1) now.year - 1 else now.year
                date.monthNumber == lastMonth && date.year == lastMonthYear
            }
        }

        val filtered = if (query.isBlank()) filteredByDate
        else filteredByDate.filter {
            it.invoice.invoiceNumber.contains(query, ignoreCase = true) ||
                    it.customerName.contains(query, ignoreCase = true)
        }

        InvoicesUiState(
            invoices = filtered,
            searchQuery = query,
            selectedFilter = filter,
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

    fun onFilterChange(filter: InvoiceFilter) {
        _selectedFilter.update { filter }
    }

    fun onTogglePin(invoiceId: String, currentPinnedState: Boolean) {
        viewModelScope.launch {
            togglePinInvoiceUseCase(invoiceId, !currentPinnedState)
        }
    }

    fun onDeleteInvoice(invoiceId: String) {
        viewModelScope.launch {
            deleteInvoiceUseCase(invoiceId)
        }
    }
}

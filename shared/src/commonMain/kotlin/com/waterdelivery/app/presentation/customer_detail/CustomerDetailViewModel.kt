package com.waterdelivery.app.presentation.customer_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterdelivery.app.core.platform.ShareManager
import com.waterdelivery.app.domain.repository.DeliveryRepository
import com.waterdelivery.app.domain.usecase.GenerateInvoiceUseCase
import com.waterdelivery.app.domain.usecase.GetCustomerSummaryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class CustomerDetailViewModel(
    private val getCustomerSummaryUseCase: GetCustomerSummaryUseCase,
    private val generateInvoiceUseCase: GenerateInvoiceUseCase,
    private val deliveryRepository: DeliveryRepository,
    private val shareManager: ShareManager,
    private val customerId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomerDetailUiState())
    val uiState: StateFlow<CustomerDetailUiState> = _uiState.asStateFlow()

    init {
        loadCustomerDetails()
    }

    private fun loadCustomerDetails() {
        combine(
            getCustomerSummaryUseCase(customerId),
            deliveryRepository.getDeliveriesForCustomer(customerId)
        ) { summary, history ->
            _uiState.update {
                it.copy(
                    summary = summary,
                    deliveryHistory = history.take(10), // Recent 10
                    isLoading = false
                )
            }
        }.catch { e ->
            _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
        }.launchIn(viewModelScope)
    }

    fun generateInvoice(startDate: Long, endDate: Long, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = generateInvoiceUseCase(customerId, startDate, endDate)
            result.onSuccess { invoice ->
                _uiState.update { it.copy(isLoading = false) }
                onSuccess(invoice.id)
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun generateInvoiceForCurrentMonth(onSuccess: (String) -> Unit) {
        val now = Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds())
        val tz = TimeZone.currentSystemDefault()
        val localNow = now.toLocalDateTime(tz)
        val startOfMonth = LocalDateTime(localNow.year, localNow.month, 1, 0, 0)
            .toInstant(tz).toEpochMilliseconds()
        val endOfMonth = now.toEpochMilliseconds()

        generateInvoice(startOfMonth, endOfMonth, onSuccess)
    }

    fun makeCall() {
        uiState.value.summary?.customer?.phoneNumber?.let {
            shareManager.makeCall(it)
        }
    }

    fun openWhatsApp() {
        uiState.value.summary?.customer?.let { customer ->
            val message = "Hello ${customer.name}, this is from AquaLog Delivery."
            shareManager.openWhatsApp(customer.phoneNumber, message)
        }
    }
}

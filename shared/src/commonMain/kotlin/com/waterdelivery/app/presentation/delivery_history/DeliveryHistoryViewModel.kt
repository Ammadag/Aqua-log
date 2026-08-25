package com.waterdelivery.app.presentation.delivery_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterdelivery.app.domain.repository.CustomerRepository
import com.waterdelivery.app.domain.usecase.GetDeliveryHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.Clock
import kotlinx.datetime.Instant as DateInstant

@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryHistoryViewModel(
    private val customerId: String,
    private val customerRepository: CustomerRepository,
    private val getDeliveryHistoryUseCase: GetDeliveryHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeliveryHistoryUiState())
    val uiState: StateFlow<DeliveryHistoryUiState> = _uiState.asStateFlow()

    private val filterFlow = MutableStateFlow<DateFilter>(DateFilter.Monthly)

    init {
        loadCustomerInfo()
        observeDeliveries()
    }

    private fun loadCustomerInfo() {
        viewModelScope.launch {
            val customer = customerRepository.getCustomerById(customerId)
            _uiState.update { it.copy(customerName = customer?.name ?: "Unknown Customer") }
        }
    }

    private fun observeDeliveries() {
        filterFlow.onEach { filter ->
            _uiState.update { it.copy(dateFilter = filter, isLoading = true) }
        }.flatMapLatest { filter ->
            val range = calculateRange(filter)
            getDeliveryHistoryUseCase(customerId, range.first, range.second)
        }.onEach { deliveries ->
            _uiState.update { it.copy(deliveries = deliveries, isLoading = false) }
        }.catch { e ->
            _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
        }.launchIn(viewModelScope)
    }

    fun onFilterChange(filter: DateFilter) {
        filterFlow.value = filter
    }

    private fun calculateRange(filter: DateFilter): Pair<Long, Long> {
        val tz = TimeZone.currentSystemDefault()
        val nowMs = Clock.System.now().toEpochMilliseconds()
        val now = DateInstant.fromEpochMilliseconds(nowMs)
        val localNow = now.toLocalDateTime(tz)

        return when (filter) {
            DateFilter.Weekly -> {
                val start = now.minus(7, DateTimeUnit.DAY, tz)
                start.toEpochMilliseconds() to now.toEpochMilliseconds()
            }
            DateFilter.Monthly -> {
                val startOfMonth = kotlinx.datetime.LocalDate(localNow.year, localNow.month, 1)
                val startInstant = startOfMonth.atStartOfDayIn(tz)
                startInstant.toEpochMilliseconds() to now.toEpochMilliseconds()
            }
            is DateFilter.Custom -> {
                filter.startDate to filter.endDate
            }
        }
    }
}

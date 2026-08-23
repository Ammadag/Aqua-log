package com.waterdelivery.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterdelivery.app.domain.repository.CustomerRepository
import com.waterdelivery.app.domain.usecase.GetDashboardStatsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlin.time.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DashboardViewModel(
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase,
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        val greeting = getGreeting()
        _uiState.update { it.copy(greeting = greeting, isLoading = true) }

        combine(
            getDashboardStatsUseCase(),
            customerRepository.getAllCustomers()
        ) { stats, customers ->
            _uiState.update {
                it.copy(
                    stats = stats,
                    recentCustomers = customers.take(5), // Show top 5 recent/active
                    isLoading = false
                )
            }
        }.catch { e ->
            _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
        }.launchIn(viewModelScope)
    }

    private fun getGreeting(): String {
        val now = Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds()).toLocalDateTime(TimeZone.currentSystemDefault())
        return when (now.hour) {
            in 0..11 -> "Good Morning, Delivery Pro"
            in 12..16 -> "Good Afternoon, Delivery Pro"
            else -> "Good Evening, Delivery Pro"
        }
    }
}

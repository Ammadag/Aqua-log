package com.waterdelivery.app.domain.usecase

import com.waterdelivery.app.domain.model.DashboardStats
import com.waterdelivery.app.domain.repository.CustomerRepository
import com.waterdelivery.app.domain.repository.DeliveryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetDashboardStatsUseCase(
    private val customerRepository: CustomerRepository,
    private val deliveryRepository: DeliveryRepository
) {
    operator fun invoke(): Flow<DashboardStats> {
        return combine(
            deliveryRepository.getTodayDeliveries(),
            customerRepository.getAllCustomers()
        ) { todayDeliveries, allCustomers ->
            DashboardStats(
                bottlesDeliveredToday = todayDeliveries.sumOf { it.deliveredQuantity },
                bottlesReturnedToday = todayDeliveries.sumOf { it.returnedQuantity },
                totalAmountToday = todayDeliveries.sumOf { it.totalAmount },
                totalActiveCustomers = allCustomers.count { it.isActive }
            )
        }
    }
}

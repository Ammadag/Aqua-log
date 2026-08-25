package com.waterdelivery.app.domain.usecase

import com.waterdelivery.app.domain.model.Delivery
import com.waterdelivery.app.domain.repository.DeliveryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetDeliveryHistoryUseCase(
    private val deliveryRepository: DeliveryRepository
) {
    operator fun invoke(customerId: String, startDate: Long, endDate: Long): Flow<List<Delivery>> {
        return deliveryRepository.getDeliveriesForCustomer(customerId).map { deliveries ->
            deliveries.filter { it.date in startDate..endDate }
                .sortedByDescending { it.date }
        }
    }
}

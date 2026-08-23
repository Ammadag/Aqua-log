package com.waterdelivery.app.domain.usecase

import com.waterdelivery.app.domain.model.CustomerSummary
import com.waterdelivery.app.domain.repository.CustomerRepository
import com.waterdelivery.app.domain.repository.DeliveryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class GetCustomerSummaryUseCase(
    private val customerRepository: CustomerRepository,
    private val deliveryRepository: DeliveryRepository
) {
    operator fun invoke(customerId: String): Flow<CustomerSummary?> {
        return deliveryRepository.getDeliveriesForCustomer(customerId).map { deliveries ->
            val customer = customerRepository.getCustomerById(customerId) ?: return@map null
            
            val tz = TimeZone.currentSystemDefault()
            val now = Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds()).toLocalDateTime(tz)
            
            val monthDeliveries = deliveries.filter {
                val deliveryDate = Instant.fromEpochMilliseconds(it.date).toLocalDateTime(tz)
                deliveryDate.year == now.year && deliveryDate.month == now.month
            }
            
            CustomerSummary(
                customer = customer,
                totalDeliveredThisMonth = monthDeliveries.sumOf { it.deliveredQuantity },
                totalReturnedThisMonth = monthDeliveries.sumOf { it.returnedQuantity },
                currentBottleBalance = deliveries.sumOf { it.deliveredQuantity - it.returnedQuantity },
                totalAmountThisMonth = monthDeliveries.sumOf { it.totalAmount }
            )
        }
    }
}

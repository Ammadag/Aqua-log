package com.waterdelivery.app.domain.usecase

import com.waterdelivery.app.domain.model.Invoice
import com.waterdelivery.app.domain.repository.BusinessProfileRepository
import com.waterdelivery.app.domain.repository.DeliveryRepository
import com.waterdelivery.app.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlin.time.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class GenerateInvoiceUseCase(
    private val deliveryRepository: DeliveryRepository,
    private val invoiceRepository: InvoiceRepository,
    private val businessProfileRepository: BusinessProfileRepository
) {
    suspend operator fun invoke(
        customerId: String,
        startDate: Long,
        endDate: Long
    ): Result<Invoice> {
        val deliveries = deliveryRepository.getDeliveriesForCustomerInRange(customerId, startDate, endDate)
        
        if (deliveries.isEmpty()) {
            return Result.failure(IllegalStateException("No deliveries found in the selected range"))
        }

        val totalDelivered = deliveries.sumOf { it.deliveredQuantity }
        val totalReturned = deliveries.sumOf { it.returnedQuantity }
        val totalAmount = deliveries.sumOf { it.totalAmount }

        val profile = businessProfileRepository.getBusinessProfile().firstOrNull()
        val prefix = profile?.invoicePrefix ?: "INV"
        
        val now = Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds())
        val timestamp = now.toEpochMilliseconds()
        val dateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        
        // Generate number like INV-2026-1692812345
        val invoiceNumber = "$prefix-${dateTime.year}-$timestamp"

        val invoice = Invoice(
            id = timestamp.toString(),
            invoiceNumber = invoiceNumber,
            customerId = customerId,
            startDate = startDate,
            endDate = endDate,
            totalDelivered = totalDelivered,
            totalReturned = totalReturned,
            totalAmount = totalAmount,
            createdAt = timestamp
        )

        return try {
            invoiceRepository.saveInvoice(invoice)
            Result.success(invoice)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

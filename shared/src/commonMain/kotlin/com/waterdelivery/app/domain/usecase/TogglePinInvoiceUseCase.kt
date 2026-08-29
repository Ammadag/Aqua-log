package com.waterdelivery.app.domain.usecase

import com.waterdelivery.app.domain.repository.InvoiceRepository

class TogglePinInvoiceUseCase(
    private val invoiceRepository: InvoiceRepository
) {
    suspend operator fun invoke(id: String, isPinned: Boolean) {
        invoiceRepository.togglePinInvoice(id, isPinned)
    }
}

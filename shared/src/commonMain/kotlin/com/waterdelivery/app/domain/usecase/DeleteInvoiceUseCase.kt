package com.waterdelivery.app.domain.usecase

import com.waterdelivery.app.domain.repository.InvoiceRepository

class DeleteInvoiceUseCase(
    private val invoiceRepository: InvoiceRepository
) {
    suspend operator fun invoke(id: String) {
        invoiceRepository.deleteInvoice(id)
    }
}

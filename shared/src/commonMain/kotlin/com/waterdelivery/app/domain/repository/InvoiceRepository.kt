package com.waterdelivery.app.domain.repository

import com.waterdelivery.app.domain.model.Invoice
import kotlinx.coroutines.flow.Flow

interface InvoiceRepository {
    suspend fun saveInvoice(invoice: Invoice)
    fun getAllInvoices(): Flow<List<Invoice>>
    fun getInvoicesForCustomer(customerId: String): Flow<List<Invoice>>
    suspend fun getInvoiceById(id: String): Invoice?
    suspend fun togglePinInvoice(id: String, isPinned: Boolean)
    suspend fun deleteInvoice(id: String)
}

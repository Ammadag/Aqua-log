package com.waterdelivery.app.data.repository

import com.waterdelivery.app.data.local.dao.InvoiceDao
import com.waterdelivery.app.data.local.mapper.toDomain
import com.waterdelivery.app.data.local.mapper.toEntity
import com.waterdelivery.app.domain.model.Invoice
import com.waterdelivery.app.domain.repository.InvoiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InvoiceRepositoryImpl(
    private val invoiceDao: InvoiceDao
) : InvoiceRepository {

    override suspend fun saveInvoice(invoice: Invoice) {
        invoiceDao.insertInvoice(invoice.toEntity())
    }

    override fun getAllInvoices(): Flow<List<Invoice>> {
        return invoiceDao.getAllInvoices().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getInvoicesForCustomer(customerId: String): Flow<List<Invoice>> {
        return invoiceDao.getInvoicesForCustomer(customerId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getInvoiceById(id: String): Invoice? {
        return invoiceDao.getInvoiceById(id)?.toDomain()
    }
}

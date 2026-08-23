package com.waterdelivery.app.data.local.mapper

import com.waterdelivery.app.data.local.entity.InvoiceEntity
import com.waterdelivery.app.domain.model.Invoice

fun InvoiceEntity.toDomain(): Invoice = Invoice(
    id = id,
    invoiceNumber = invoiceNumber,
    customerId = customerId,
    startDate = startDate,
    endDate = endDate,
    totalDelivered = totalDelivered,
    totalReturned = totalReturned,
    totalAmount = totalAmount,
    createdAt = createdAt,
)

fun Invoice.toEntity(): InvoiceEntity = InvoiceEntity(
    id = id,
    invoiceNumber = invoiceNumber,
    customerId = customerId,
    startDate = startDate,
    endDate = endDate,
    totalDelivered = totalDelivered,
    totalReturned = totalReturned,
    totalAmount = totalAmount,
    createdAt = createdAt,
)

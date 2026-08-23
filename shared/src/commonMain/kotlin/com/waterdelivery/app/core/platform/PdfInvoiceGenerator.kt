package com.waterdelivery.app.core.platform

import com.waterdelivery.app.domain.model.BusinessProfile
import com.waterdelivery.app.domain.model.Customer
import com.waterdelivery.app.domain.model.Delivery
import com.waterdelivery.app.domain.model.Invoice

expect interface PdfInvoiceGenerator {
    suspend fun generateInvoicePdf(
        invoice: Invoice,
        customer: Customer,
        profile: BusinessProfile,
        deliveries: List<Delivery>
    ): String
}

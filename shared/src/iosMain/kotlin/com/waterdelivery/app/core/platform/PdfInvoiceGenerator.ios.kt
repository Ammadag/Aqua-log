package com.waterdelivery.app.core.platform

import com.waterdelivery.app.domain.model.BusinessProfile
import com.waterdelivery.app.domain.model.Customer
import com.waterdelivery.app.domain.model.Delivery
import com.waterdelivery.app.domain.model.Invoice

actual interface PdfInvoiceGenerator {
    actual suspend fun generateInvoicePdf(
        invoice: Invoice,
        customer: Customer,
        profile: BusinessProfile,
        deliveries: List<Delivery>
    ): String
}

class IosPdfInvoiceGenerator : PdfInvoiceGenerator {
    override suspend fun generateInvoicePdf(
        invoice: Invoice,
        customer: Customer,
        profile: BusinessProfile,
        deliveries: List<Delivery>
    ): String {
        // iOS PDF generation stub
        return "ios_invoice_placeholder"
    }
}

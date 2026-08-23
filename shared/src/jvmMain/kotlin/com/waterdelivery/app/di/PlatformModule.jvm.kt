package com.waterdelivery.app.di

import com.waterdelivery.app.core.platform.ContactManager
import com.waterdelivery.app.core.platform.PdfInvoiceGenerator
import com.waterdelivery.app.core.platform.ShareManager
import com.waterdelivery.app.domain.model.BusinessProfile
import com.waterdelivery.app.domain.model.Customer
import com.waterdelivery.app.domain.model.Delivery
import com.waterdelivery.app.domain.model.Invoice
import com.waterdelivery.app.presentation.contact_picker.ContactItem
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<PdfInvoiceGenerator> { 
        object : PdfInvoiceGenerator {
            override suspend fun generateInvoicePdf(
                invoice: Invoice,
                customer: Customer,
                profile: BusinessProfile,
                deliveries: List<Delivery>
            ): String = ""
        }
    }
    single<ShareManager> {
        object : ShareManager {
            override fun shareFile(filePath: String, mimeType: String) {}
            override fun shareFileViaWhatsApp(filePath: String, phoneNumber: String) {}
            override fun makeCall(phoneNumber: String) {}
            override fun openWhatsApp(phoneNumber: String, message: String) {}
        }
    }
    single<ContactManager> {
        object : ContactManager {
            override suspend fun getContacts(): List<ContactItem> = emptyList()
            override fun hasContactPermission(): Boolean = false
        }
    }
}

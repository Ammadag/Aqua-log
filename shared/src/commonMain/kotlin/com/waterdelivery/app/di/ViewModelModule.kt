package com.waterdelivery.app.di

import com.waterdelivery.app.presentation.add_customer.AddCustomerViewModel
import com.waterdelivery.app.presentation.add_delivery.AddDeliveryViewModel
import com.waterdelivery.app.presentation.contact_picker.ContactPickerViewModel
import com.waterdelivery.app.presentation.customer_detail.CustomerDetailViewModel
import com.waterdelivery.app.presentation.customers.CustomersViewModel
import com.waterdelivery.app.presentation.dashboard.DashboardViewModel
import com.waterdelivery.app.presentation.delivery_history.DeliveryHistoryViewModel
import com.waterdelivery.app.presentation.invoice_preview.InvoicePreviewViewModel
import com.waterdelivery.app.presentation.invoices.InvoicesViewModel
import com.waterdelivery.app.presentation.onboarding.OnboardingViewModel
import com.waterdelivery.app.presentation.settings.SettingsViewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory { DashboardViewModel(get(), get()) }
    factory { CustomersViewModel(get(), get()) }
    factory { AddCustomerViewModel(get()) }
    factory { ContactPickerViewModel(get()) }
    factory { (customerId: String?) -> AddDeliveryViewModel(get(), get(), get(), customerId) }
    factory { (customerId: String) -> CustomerDetailViewModel(get(), get(), get(), get(), get(), customerId) }
    factory { (customerId: String) -> DeliveryHistoryViewModel(customerId, get(), get()) }
    factory { InvoicesViewModel(get(), get(), get(), get()) }
    factory { (invoiceId: String) -> InvoicePreviewViewModel(get(), get(), get(), get(), get(), get(), invoiceId) }
    factory { SettingsViewModel(get(), get()) }
    factory { OnboardingViewModel(get(), get()) }
}

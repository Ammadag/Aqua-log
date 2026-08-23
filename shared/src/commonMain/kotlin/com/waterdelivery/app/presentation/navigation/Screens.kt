package com.waterdelivery.app.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Dashboard : Screen("dashboard")
    object Customers : Screen("customers")
    object CustomerDetail : Screen("customer_detail/{customerId}") {
        fun createRoute(customerId: String) = "customer_detail/$customerId"
    }
    object AddCustomer : Screen("add_customer")
    object ContactPicker : Screen("contact_picker")
    object AddDelivery : Screen("add_delivery?customerId={customerId}") {
        fun createRoute(customerId: String? = null) = if (customerId != null) "add_delivery?customerId=$customerId" else "add_delivery"
    }
    object Invoices : Screen("invoices")
    object InvoicePreview : Screen("invoice_preview/{invoiceId}") {
        fun createRoute(invoiceId: String) = "invoice_preview/$invoiceId"
    }
    object Settings : Screen("settings")
}

package com.waterdelivery.app.presentation.invoice_preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterdelivery.app.core.platform.PdfInvoiceGenerator
import com.waterdelivery.app.core.platform.ShareManager
import com.waterdelivery.app.domain.repository.BusinessProfileRepository
import com.waterdelivery.app.domain.repository.CustomerRepository
import com.waterdelivery.app.domain.repository.DeliveryRepository
import com.waterdelivery.app.domain.repository.InvoiceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InvoicePreviewViewModel(
    private val invoiceRepository: InvoiceRepository,
    private val customerRepository: CustomerRepository,
    private val businessProfileRepository: BusinessProfileRepository,
    private val deliveryRepository: DeliveryRepository,
    private val pdfGenerator: PdfInvoiceGenerator,
    private val shareManager: ShareManager,
    private val invoiceId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(InvoicePreviewUiState())
    val uiState: StateFlow<InvoicePreviewUiState> = _uiState.asStateFlow()

    init {
        loadInvoiceData()
    }

    private fun loadInvoiceData() {
        viewModelScope.launch {
            try {
                val invoice = invoiceRepository.getInvoiceById(invoiceId) ?: throw Exception("Invoice not found")
                val customer = customerRepository.getCustomerById(invoice.customerId)
                val profile = businessProfileRepository.getBusinessProfile().firstOrNull()
                val deliveries = deliveryRepository.getDeliveriesForCustomerInRange(
                    invoice.customerId, invoice.startDate, invoice.endDate
                )
                
                _uiState.update { 
                    it.copy(
                        invoice = invoice,
                        customer = customer,
                        businessProfile = profile,
                        deliveries = deliveries,
                        isLoading = false
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun shareInvoice() {
        val state = _uiState.value
        if (state.isLoading) return
        
        if (state.invoice == null || state.customer == null || state.businessProfile == null) {
            val missing = mutableListOf<String>()
            if (state.invoice == null) missing.add("invoice")
            if (state.customer == null) missing.add("customer")
            if (state.businessProfile == null) missing.add("business profile")
            
            _uiState.update { it.copy(errorMessage = "Cannot share: ${missing.joinToString(", ")} data is missing") }
            return
        }

        val invoice = state.invoice
        val customer = state.customer
        val profile = state.businessProfile
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                // Small delay to ensure UI shows loading state
                delay(500)
                val path = pdfGenerator.generateInvoicePdf(invoice, customer, profile, state.deliveries)
                shareManager.shareFileViaWhatsApp(path, customer.phoneNumber)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message ?: "An unknown error occurred") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

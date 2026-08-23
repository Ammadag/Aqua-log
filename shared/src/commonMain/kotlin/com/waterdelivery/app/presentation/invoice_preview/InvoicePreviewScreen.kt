package com.waterdelivery.app.presentation.invoice_preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.waterdelivery.app.domain.model.BusinessProfile
import com.waterdelivery.app.domain.model.Customer
import com.waterdelivery.app.domain.model.Delivery
import com.waterdelivery.app.domain.model.Invoice
import com.waterdelivery.app.presentation.ui.components.AppPrimaryButton
import com.waterdelivery.app.presentation.ui.components.AppTopBar
import com.waterdelivery.app.presentation.ui.theme.Spacing
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun InvoicePreviewScreen(
    viewModel: InvoicePreviewViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Invoice Preview",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.large),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
                ) {
                    AppPrimaryButton(
                        text = "SHARE VIA WHATSAPP",
                        onClick = viewModel::shareInvoice,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                uiState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(Spacing.large)
                    )
                }

                uiState.invoice?.let { invoice ->
                    InvoiceDocument(
                        invoice = invoice,
                        customer = uiState.customer,
                        profile = uiState.businessProfile,
                        deliveries = uiState.deliveries
                    )
                }
            }
        }
    }
}

@Composable
private fun InvoiceDocument(
    invoice: Invoice,
    customer: Customer?,
    profile: BusinessProfile?,
    deliveries: List<Delivery>
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.large)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(Spacing.large)
    ) {
        // Business Header
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = profile?.businessName ?: "AquaLog Delivery",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(text = profile?.address ?: "Location Address", style = MaterialTheme.typography.bodySmall)
            Text(text = "Phone: ${profile?.phone ?: ""}", style = MaterialTheme.typography.bodySmall)
        }

        HorizontalDivider()

        // Invoice Meta
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Invoice Number", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(invoice.invoiceNumber, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Issue Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(formatEpoch(invoice.createdAt), style = MaterialTheme.typography.bodyLarge)
            }
        }

        // Bill To
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ) {
            Column(modifier = Modifier.padding(Spacing.medium)) {
                Text("BILL TO", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(Spacing.extraSmall))
                Text(customer?.name ?: "Unknown Customer", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(customer?.address ?: "", style = MaterialTheme.typography.bodySmall)
                Text(customer?.phoneNumber ?: "", style = MaterialTheme.typography.bodySmall)
            }
        }

        // Items Table
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.small)) {
            TableHeader()
            HorizontalDivider(thickness = 1.dp)
            deliveries.forEach { delivery ->
                TableRow(delivery)
            }
        }

        HorizontalDivider()

        // Financial Summary
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(Spacing.extraSmall)
        ) {
            SummaryRow("Total Delivered", "${invoice.totalDelivered} Bottles")
            SummaryRow("Total Returned", "${invoice.totalReturned} Empties")
            SummaryRow("Net Balance", "${invoice.totalDelivered - invoice.totalReturned} Bottles")
            Spacer(modifier = Modifier.height(Spacing.small))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Grand Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "PKR ${invoice.totalAmount.toInt()}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(Spacing.huge))
    }
}

@Composable
private fun TableHeader() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Date", modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Text("Del", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Text("Ret", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Text("Amt", modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.End)
    }
}

@Composable
private fun TableRow(delivery: Delivery) {
    val date = Instant.fromEpochMilliseconds(delivery.date).toLocalDateTime(TimeZone.currentSystemDefault())
    val dateStr = "${date.dayOfMonth}/${date.monthNumber}"
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(dateStr, modifier = Modifier.weight(2f), style = MaterialTheme.typography.bodySmall)
        Text("${delivery.deliveredQuantity}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        Text("${delivery.returnedQuantity}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        Text("${delivery.totalAmount.toInt()}", modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, textAlign = androidx.compose.ui.text.style.TextAlign.End)
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(0.6f), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
    }
}

private fun formatEpoch(epoch: Long): String {
    val date = Instant.fromEpochMilliseconds(epoch).toLocalDateTime(TimeZone.currentSystemDefault())
    return "${date.dayOfMonth} ${date.month.name.take(3)} ${date.year}"
}

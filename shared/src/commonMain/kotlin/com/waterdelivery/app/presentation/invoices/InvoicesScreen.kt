package com.waterdelivery.app.presentation.invoices

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.waterdelivery.app.presentation.ui.components.AppTopBar
import com.waterdelivery.app.presentation.ui.components.EmptyState
import com.waterdelivery.app.presentation.ui.components.InvoiceCard
import com.waterdelivery.app.presentation.ui.components.SearchBar
import com.waterdelivery.app.presentation.ui.components.SwipeableActionCard
import com.waterdelivery.app.presentation.ui.theme.Spacing
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun InvoicesScreen(
    viewModel: InvoicesViewModel,
    onInvoiceClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var invoiceToDelete by remember { mutableStateOf<InvoiceItemUiModel?>(null) }

    if (invoiceToDelete != null) {
        AlertDialog(
            onDismissRequest = { invoiceToDelete = null },
            title = { Text("Delete Invoice") },
            text = { Text("Are you sure you want to delete invoice ${invoiceToDelete?.invoice?.invoiceNumber}? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        invoiceToDelete?.let { viewModel.onDeleteInvoice(it.invoice.id) }
                        invoiceToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { invoiceToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Invoices",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.large, vertical = Spacing.medium)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                FilterChip(
                    selected = uiState.selectedFilter == InvoiceFilter.ALL,
                    onClick = { viewModel.onFilterChange(InvoiceFilter.ALL) },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = uiState.selectedFilter == InvoiceFilter.THIS_MONTH,
                    onClick = { viewModel.onFilterChange(InvoiceFilter.THIS_MONTH) },
                    label = { Text("This Month") }
                )
                FilterChip(
                    selected = uiState.selectedFilter == InvoiceFilter.LAST_MONTH,
                    onClick = { viewModel.onFilterChange(InvoiceFilter.LAST_MONTH) },
                    label = { Text("Last Month") }
                )
            }

            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                placeholder = "Search invoices...",
                modifier = Modifier.padding(Spacing.large)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.invoices.isEmpty()) {
                    EmptyState(
                        message = if (uiState.searchQuery.isEmpty()) "No invoices generated yet"
                        else "No invoices found for \"${uiState.searchQuery}\"",
                        actionLabel = if (uiState.searchQuery.isEmpty()) "Back to Dashboard" else null,
                        onActionClick = if (uiState.searchQuery.isEmpty()) onBackClick else null
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = Spacing.large,
                            end = Spacing.large,
                            bottom = Spacing.huge
                        ),
                        verticalArrangement = Arrangement.spacedBy(Spacing.medium)
                    ) {
                        items(uiState.invoices, key = { it.invoice.id }) { item ->
                            val invoice = item.invoice
                            val startD = Instant.fromEpochMilliseconds(invoice.startDate)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                            val endD = Instant.fromEpochMilliseconds(invoice.endDate)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                            val periodStr =
                                "${startD.dayOfMonth} ${startD.month.name.take(3)} – ${endD.dayOfMonth} ${
                                    endD.month.name.take(3)
                                }"

                            SwipeableActionCard(
                                isPinned = invoice.isPinned,
                                onPin = { viewModel.onTogglePin(invoice.id, invoice.isPinned) },
                                onDelete = { invoiceToDelete = item }
                            ) {
                                InvoiceCard(
                                    invoiceNumber = invoice.invoiceNumber,
                                    period = periodStr,
                                    amount = invoice.totalAmount,
                                    customerName = item.customerName,
                                    volume = invoice.totalDelivered,
                                    isPinned = invoice.isPinned,
                                    onClick = { onInvoiceClick(invoice.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

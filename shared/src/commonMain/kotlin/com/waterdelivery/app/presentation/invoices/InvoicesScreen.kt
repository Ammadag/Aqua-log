package com.waterdelivery.app.presentation.invoices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.waterdelivery.app.presentation.ui.components.AppTopBar
import com.waterdelivery.app.presentation.ui.components.EmptyState
import com.waterdelivery.app.presentation.ui.components.InvoiceCard
import com.waterdelivery.app.presentation.ui.components.SearchBar
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
                            val date = Instant.fromEpochMilliseconds(invoice.createdAt)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                            val dateStr = "${date.dayOfMonth} ${date.month.name.take(3)} ${date.year}"
                            
                            InvoiceCard(
                                invoiceNumber = invoice.invoiceNumber,
                                date = dateStr,
                                amount = invoice.totalAmount,
                                customerName = item.customerName,
                                onClick = { onInvoiceClick(invoice.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

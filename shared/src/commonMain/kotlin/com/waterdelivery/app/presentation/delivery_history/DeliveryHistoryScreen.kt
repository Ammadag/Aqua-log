package com.waterdelivery.app.presentation.delivery_history

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.waterdelivery.app.presentation.ui.components.AppTopBar
import com.waterdelivery.app.presentation.ui.components.DeliveryCard
import com.waterdelivery.app.presentation.ui.components.EmptyState
import com.waterdelivery.app.presentation.ui.theme.Spacing
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryHistoryScreen(
    viewModel: DeliveryHistoryViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDateRangePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Delivery History",
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
            Column(modifier = Modifier.padding(Spacing.large)) {
                Text(
                    text = uiState.customerName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.medium),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.small)
                ) {
                    FilterChip(
                        selected = uiState.dateFilter == DateFilter.Weekly,
                        onClick = { viewModel.onFilterChange(DateFilter.Weekly) },
                        label = { Text("Weekly") }
                    )
                    FilterChip(
                        selected = uiState.dateFilter == DateFilter.Monthly,
                        onClick = { viewModel.onFilterChange(DateFilter.Monthly) },
                        label = { Text("Monthly") }
                    )
                    FilterChip(
                        selected = uiState.dateFilter is DateFilter.Custom,
                        onClick = { showDateRangePicker = true },
                        label = {
                            val label =
                                if (uiState.dateFilter is DateFilter.Custom) "Custom Range" else "Custom"
                            Text(label)
                        }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.deliveries.isEmpty()) {
                    EmptyState(
                        message = "No deliveries found for the selected range.",
                        actionLabel = "Try different filter",
                        onActionClick = { viewModel.onFilterChange(DateFilter.Monthly) }
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
                        items(uiState.deliveries, key = { it.id }) { delivery ->
                            val date = Instant.fromEpochMilliseconds(delivery.date)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                            val dateStr =
                                "${date.dayOfMonth} ${date.month.name.take(3)} ${date.year}"

                            DeliveryCard(
                                date = dateStr,
                                deliveredQty = delivery.deliveredQuantity,
                                returnedQty = delivery.returnedQuantity,
                                totalAmount = delivery.totalAmount
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDateRangePicker) {
        val state = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val start = state.selectedStartDateMillis
                        val end = state.selectedEndDateMillis
                        if (start != null && end != null) {
                            viewModel.onFilterChange(DateFilter.Custom(start, end))
                            showDateRangePicker = false
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DateRangePicker(
                state = state,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

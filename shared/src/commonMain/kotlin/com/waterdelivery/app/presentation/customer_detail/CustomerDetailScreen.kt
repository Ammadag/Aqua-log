package com.waterdelivery.app.presentation.customer_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.waterdelivery.app.domain.model.CustomerSummary
import com.waterdelivery.app.domain.model.Delivery
import com.waterdelivery.app.presentation.ui.components.AppPrimaryButton
import com.waterdelivery.app.presentation.ui.components.AppSecondaryButton
import com.waterdelivery.app.presentation.ui.components.AppTopBar
import com.waterdelivery.app.presentation.ui.components.DeliveryCard
import com.waterdelivery.app.presentation.ui.theme.Spacing
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun CustomerDetailScreen(
    viewModel: CustomerDetailViewModel,
    onNavigateToAddDelivery: (String) -> Unit,
    onNavigateToGenerateInvoice: (String) -> Unit,
    onNavigateToDeliveryHistory: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Customer Details",
                showBackButton = true,
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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

                uiState.summary?.let { summary ->
                    CustomerDetailContent(
                        summary = summary,
                        history = uiState.deliveryHistory,
                        onAddDelivery = { onNavigateToAddDelivery(summary.customer.id) },
                        onGenerateInvoice = {
                            viewModel.generateInvoiceForCurrentMonth { invoiceId ->
                                onNavigateToGenerateInvoice(invoiceId)
                            }
                        },
                        onViewAllHistory = { onNavigateToDeliveryHistory(summary.customer.id) },
                        onCallClick = viewModel::makeCall,
                        onWhatsAppClick = viewModel::openWhatsApp
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomerDetailContent(
    summary: CustomerSummary,
    history: List<Delivery>,
    onAddDelivery: () -> Unit,
    onGenerateInvoice: () -> Unit,
    onViewAllHistory: () -> Unit,
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.large),
        verticalArrangement = Arrangement.spacedBy(Spacing.large)
    ) {
        item {
            Spacer(modifier = Modifier.height(Spacing.small))
            CustomerInfoHeader(
                name = summary.customer.name,
                phone = summary.customer.phoneNumber,
                address = summary.customer.address,
                onCallClick = onCallClick,
                onWhatsAppClick = onWhatsAppClick
            )
        }

        item {
            BottleSummarySection(
                delivered = summary.totalDeliveredThisMonth,
                returned = summary.totalReturnedThisMonth,
                balance = summary.currentBottleBalance
            )
        }

        item {
            AppPrimaryButton(
                text = "ADD DELIVERY",
                onClick = onAddDelivery
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Delivery History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onViewAllHistory) {
                    Text(
                        "View All",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        items(history, key = { it.id }) { delivery ->
            val date = Instant.fromEpochMilliseconds(delivery.date)
                .toLocalDateTime(TimeZone.currentSystemDefault())
            val dateStr = "${date.dayOfMonth} ${date.month.name.take(3)} ${date.year}"

            DeliveryCard(
                date = dateStr,
                deliveredQty = delivery.deliveredQuantity,
                returnedQty = delivery.returnedQuantity,
                totalAmount = delivery.totalAmount
            )
        }

        item {
            AppSecondaryButton(
                text = "GENERATE INVOICE",
                onClick = onGenerateInvoice
            )
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.huge))
        }
    }
}

@Composable
private fun CustomerInfoHeader(
    name: String,
    phone: String,
    address: String,
    onCallClick: () -> Unit,
    onWhatsAppClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(Spacing.large),
            verticalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Call,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = phone,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        null,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                ActionButton(
                    icon = Icons.Default.Call,
                    label = "Call",
                    modifier = Modifier.weight(1f),
                    onClick = onCallClick
                )
                ActionButton(
                    icon = Icons.AutoMirrored.Filled.Chat,
                    label = "WhatsApp",
                    modifier = Modifier.weight(1f),
                    containerColor = Color(0xFF25D366).copy(alpha = 0.1f),
                    contentColor = Color(0xFF075E54),
                    onClick = onWhatsAppClick
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = contentColor)
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun BottleSummarySection(delivered: Int, returned: Int, balance: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(modifier = Modifier.padding(Spacing.medium)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Bottle Summary",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "This Month",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacing.medium))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
                SummaryBox(
                    value = "$delivered",
                    label = "Delivered",
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primary
                )
                SummaryBox(
                    value = "$returned",
                    label = "Returned",
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.secondary
                )
                SummaryBox(
                    value = "$balance",
                    label = "Balance",
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun SummaryBox(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    color: Color,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = containerColor
    ) {
        Column(
            modifier = Modifier.padding(vertical = Spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

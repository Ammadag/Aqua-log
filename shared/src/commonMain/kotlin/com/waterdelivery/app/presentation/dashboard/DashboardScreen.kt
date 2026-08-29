package com.waterdelivery.app.presentation.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.waterdelivery.app.presentation.ui.components.AppPrimaryButton
import com.waterdelivery.app.presentation.ui.components.AppTopBar
import com.waterdelivery.app.presentation.ui.components.CustomerCard
import com.waterdelivery.app.presentation.ui.theme.Spacing

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToAddDelivery: () -> Unit,
    onCustomerClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "AquaLog",
                actions = {
                    // Placeholder for potential profile/settings action
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                DashboardContent(
                    uiState = uiState,
                    onNavigateToAddDelivery = onNavigateToAddDelivery,
                    onCustomerClick = onCustomerClick
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onNavigateToAddDelivery: () -> Unit,
    onCustomerClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.large),
        verticalArrangement = Arrangement.spacedBy(Spacing.large)
    ) {
        item {
            Spacer(modifier = Modifier.height(Spacing.small))
            GreetingSection(uiState.greeting)
        }

        item {
            SummaryGrid(
                deliveredCount = uiState.stats.bottlesDeliveredToday,
                returnedCount = uiState.stats.bottlesReturnedToday,
                revenue = uiState.stats.totalAmountToday
            )
        }

        item {
            QuickActions(
                onAddDelivery = onNavigateToAddDelivery
            )
        }

        item {
            RecentCustomersHeader()
        }

        items(uiState.recentCustomers, key = { it.id }) { customer ->
            CustomerCard(
                customerName = customer.name,
                phone = customer.phoneNumber,
                address = customer.address,
                bottleBalance = 0, // In V1, summary balance calculation happens in summary use case, keeping it 0 for dashboard row
                onClick = { onCustomerClick(customer.id) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.huge))
        }
    }
}

@Composable
private fun GreetingSection(greeting: String) {
    Column {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Here's your delivery summary",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SummaryGrid(
    deliveredCount: Int,
    returnedCount: Int,
    revenue: Double
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
        // Large Revenue Card (Bento Style Variation)
        SummaryCardLarge(
            label = "TODAY'S REVENUE",
            value = "Rs. ${revenue.toInt()}",
            icon = Icons.Default.ReceiptLong
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            SummaryCardSmall(
                label = "DELIVERED",
                value = "$deliveredCount",
                icon = Icons.Default.WaterDrop,
                modifier = Modifier.weight(1f)
            )
            SummaryCardSmall(
                label = "RETURNED",
                value = "$returnedCount",
                icon = Icons.Default.Recycling,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryCardLarge(
    label: String,
    value: String,
    icon: ImageVector
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
        Row(
            modifier = Modifier.padding(Spacing.large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun SummaryCardSmall(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(modifier = Modifier.padding(Spacing.large)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(Spacing.extraSmall))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (label == "DELIVERED") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
private fun QuickActions(
    onAddDelivery: () -> Unit
) {
    AppPrimaryButton(
        text = "ADD DELIVERY",
        onClick = onAddDelivery
    )
}

@Composable
private fun RecentCustomersHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Recent Customers",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

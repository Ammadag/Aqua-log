package com.waterdelivery.app.presentation.customers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.waterdelivery.app.presentation.ui.components.AppTopBar
import com.waterdelivery.app.presentation.ui.components.CustomerCard
import com.waterdelivery.app.presentation.ui.components.EmptyState
import com.waterdelivery.app.presentation.ui.components.SearchBar
import com.waterdelivery.app.presentation.ui.theme.Spacing

@Composable
fun CustomersScreen(
    viewModel: CustomersViewModel,
    onNavigateToAddCustomer: () -> Unit,
    onCustomerClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Customers",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddCustomer,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Customer")
            }
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
                placeholder = "Search customers...",
                modifier = Modifier.padding(Spacing.large)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.customers.isEmpty()) {
                    EmptyState(
                        message = if (uiState.searchQuery.isEmpty()) "No customers added yet"
                        else "No customers found for \"${uiState.searchQuery}\"",
                        actionLabel = if (uiState.searchQuery.isEmpty()) "Add your first customer" else null,
                        onActionClick = if (uiState.searchQuery.isEmpty()) onNavigateToAddCustomer else null
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
                        items(uiState.customers, key = { it.id }) { customer ->
                            CustomerCard(
                                customerName = customer.name,
                                phone = customer.phoneNumber,
                                address = customer.address,
                                bottleBalance = 0, // Placeholder, will be computed in summary
                                onClick = { onCustomerClick(customer.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

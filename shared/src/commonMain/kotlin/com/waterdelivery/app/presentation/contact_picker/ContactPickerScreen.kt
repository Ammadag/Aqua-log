package com.waterdelivery.app.presentation.contact_picker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.waterdelivery.app.presentation.ui.components.AppBottomSheet
import com.waterdelivery.app.presentation.ui.components.AppPrimaryButton
import com.waterdelivery.app.presentation.ui.components.AppTopBar
import com.waterdelivery.app.presentation.ui.components.SearchBar
import com.waterdelivery.app.presentation.ui.theme.Spacing

@Composable
fun ContactPickerScreen(
    viewModel: ContactPickerViewModel,
    onContactSelected: (ContactItem) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showPermissionRequest by remember { mutableStateOf(false) }

    val handlePermissionRequest = {
        viewModel.dismissBottomSheet()
        showPermissionRequest = true
    }

    if (showPermissionRequest) {
        RequestContactPermission { granted ->
            showPermissionRequest = false
            if (granted) {
                viewModel.checkPermissionAndLoadContacts()
            } else {
                viewModel.onPermissionDenied(onRequestPermissionAgain = handlePermissionRequest)
            }
        }
    }

    AppBottomSheet(
        state = uiState.bottomSheetState,
        onDismissRequest = viewModel::dismissBottomSheet
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Select Contact",
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
            if (!uiState.hasPermission) {
                PermissionRequiredState(
                    onRequestPermission = handlePermissionRequest
                )
            } else if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.contacts.isEmpty() && uiState.searchQuery.isBlank()) {
                EmptyContactsState(
                    errorMessage = uiState.errorMessage,
                    onRetry = { viewModel.loadContacts() }
                )
            } else {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    placeholder = "Search contacts...",
                    modifier = Modifier.padding(Spacing.large)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = Spacing.huge)
                ) {
                    items(uiState.contacts, key = { it.id }) { contact ->
                        ContactRow(
                            contact = contact,
                            isSelected = uiState.selectedContact?.id == contact.id,
                            onClick = { viewModel.onContactSelect(contact) }
                        )
                    }
                }

                Box(modifier = Modifier.padding(Spacing.large)) {
                    AppPrimaryButton(
                        text = "CONTINUE WITH SELECTED CONTACT",
                        onClick = {
                            viewModel.onContinueClick(onContactConfirmed = onContactSelected)
                        },
                        enabled = uiState.selectedContact != null
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionRequiredState(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(Spacing.huge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(Spacing.large))
        Text(
            text = "Contact Permission Required",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "AquaLog needs access to your contacts to help you import customer contact information directly.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(Spacing.huge))
        AppPrimaryButton(text = "GRANT PERMISSION", onClick = onRequestPermission)
    }
}

@Composable
private fun EmptyContactsState(
    errorMessage: String? = null,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(Spacing.huge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = errorMessage ?: "No contacts found on your device.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(Spacing.large))
            AppPrimaryButton(
                text = "RETRY",
                onClick = onRetry
            )
        }
    }
}

@Composable
private fun ContactRow(
    contact: ContactItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.large, vertical = Spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
    ) {
        // Initial Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.name.firstOrNull()?.toString() ?: "?",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = contact.phoneNumber,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        RadioButton(
            selected = isSelected,
            onClick = onClick
        )
    }
}

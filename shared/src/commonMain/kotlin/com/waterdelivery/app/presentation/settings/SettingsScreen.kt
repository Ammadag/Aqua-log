package com.waterdelivery.app.presentation.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.waterdelivery.app.core.platform.rememberImagePicker
import com.waterdelivery.app.presentation.ui.components.AppPrimaryButton
import com.waterdelivery.app.presentation.ui.components.AppTopBar
import com.waterdelivery.app.presentation.ui.components.LocalImage
import com.waterdelivery.app.presentation.ui.theme.Spacing

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.isSavedSuccess) {
        if (uiState.isSavedSuccess) {
            snackbarHostState.showSnackbar("Settings saved successfully")
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Settings",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Spacing.large)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(Spacing.large)
        ) {
            val imagePicker = rememberImagePicker { viewModel.onLogoSelected(it) }

            // Section 1: Business Profile
            SettingsSectionHeader("Business Profile")

            LogoPickerCard(
                logoPath = uiState.logoPath,
                onPickImage = imagePicker
            )

            OutlinedTextField(
                value = uiState.businessName,
                onValueChange = viewModel::onBusinessNameChange,
                label = { Text("Business Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            OutlinedTextField(
                value = uiState.phone,
                onValueChange = viewModel::onPhoneChange,
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.address,
                onValueChange = viewModel::onAddressChange,
                label = { Text("Business Address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = Spacing.small),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Section 2: Delivery & Billing Defaults
            SettingsSectionHeader("Delivery & Billing Defaults")

            OutlinedTextField(
                value = uiState.defaultPrice,
                onValueChange = viewModel::onPriceChange,
                label = { Text("Default Bottle Price (PKR)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.invoicePrefix,
                onValueChange = viewModel::onPrefixChange,
                label = { Text("Invoice Prefix (e.g., INV)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = Spacing.small),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Section 3: App Information
            SettingsSectionHeader("App Information")

            AppInfoRow(
                label = "Version",
                value = uiState.appVersion,
                icon = Icons.Default.Verified
            )

            AppInfoRow(
                label = "Local Database",
                value = "Active",
                icon = Icons.Default.Info
            )

            Spacer(modifier = Modifier.height(Spacing.large))

            AppPrimaryButton(
                text = "SAVE SETTINGS",
                onClick = viewModel::saveSettings,
                enabled = !uiState.isSaving
            )

            Spacer(modifier = Modifier.height(Spacing.huge))
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.outline,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
private fun AppInfoRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun LogoPickerCard(
    logoPath: String?,
    onPickImage: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(Spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.medium)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                    .clickable { onPickImage() },
                contentAlignment = Alignment.Center
            ) {
                if (logoPath != null) {
                    LocalImage(
                        path = logoPath,
                        contentDescription = "Business Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (logoPath == null) "Add Business Logo" else "Business Logo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (logoPath == null) "Help customers identify your brand" else "Logo will appear on invoices",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedButton(
                onClick = onPickImage,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 12.dp,
                    vertical = 0.dp
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.CloudUpload, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(4.dp))
                Text("Upload", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

package com.waterdelivery.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.waterdelivery.app.presentation.ui.theme.Spacing

enum class BottomSheetType {
    SUCCESS,
    ALERT,
    ERROR,
    PERMISSION
}

data class BottomSheetUiState(
    val isVisible: Boolean = false,
    val title: String = "",
    val message: String = "",
    val type: BottomSheetType = BottomSheetType.ALERT,
    val primaryButtonText: String = "OK",
    val secondaryButtonText: String? = null,
    val onPrimaryClick: (() -> Unit)? = null,
    val onSecondaryClick: (() -> Unit)? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomSheet(
    title: String,
    message: String,
    type: BottomSheetType = BottomSheetType.ALERT,
    primaryButtonText: String = "OK",
    onPrimaryClick: () -> Unit,
    secondaryButtonText: String? = null,
    onSecondaryClick: (() -> Unit)? = null,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.large)
                .padding(bottom = Spacing.huge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val (containerColor, iconColor, iconVector) = when (type) {
                BottomSheetType.SUCCESS -> Triple(
                    MaterialTheme.colorScheme.primaryContainer,
                    MaterialTheme.colorScheme.primary,
                    Icons.Default.CheckCircle
                )
                BottomSheetType.ALERT -> Triple(
                    MaterialTheme.colorScheme.tertiaryContainer,
                    MaterialTheme.colorScheme.tertiary,
                    Icons.Default.Warning
                )
                BottomSheetType.PERMISSION -> Triple(
                    MaterialTheme.colorScheme.secondaryContainer,
                    MaterialTheme.colorScheme.secondary,
                    Icons.Default.Lock
                )
                BottomSheetType.ERROR -> Triple(
                    MaterialTheme.colorScheme.errorContainer,
                    MaterialTheme.colorScheme.error,
                    Icons.Default.Info
                )
            }

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(containerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = type.name,
                    tint = iconColor,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.large))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(Spacing.medium))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.huge))

            AppPrimaryButton(
                text = primaryButtonText,
                onClick = onPrimaryClick
            )

            if ((secondaryButtonText != null) && (onSecondaryClick != null)) {
                Spacer(modifier = Modifier.height(Spacing.medium))
                AppSecondaryButton(
                    text = secondaryButtonText,
                    onClick = onSecondaryClick
                )
            }
        }
    }
}

@Composable
fun AppBottomSheet(
    state: BottomSheetUiState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isVisible) {
        AppBottomSheet(
            title = state.title,
            message = state.message,
            type = state.type,
            primaryButtonText = state.primaryButtonText,
            onPrimaryClick = {
                state.onPrimaryClick?.invoke()
                onDismissRequest()
            },
            secondaryButtonText = state.secondaryButtonText,
            onSecondaryClick = {
                state.onSecondaryClick?.invoke()
                onDismissRequest()
            },
            onDismissRequest = onDismissRequest,
            modifier = modifier
        )
    }
}

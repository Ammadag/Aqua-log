package com.waterdelivery.app.presentation.contact_picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
actual fun RequestContactPermission(
    onResult: (Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        onResult(false)
    }
}

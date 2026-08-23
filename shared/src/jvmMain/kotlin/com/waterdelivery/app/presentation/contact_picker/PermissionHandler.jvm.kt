package com.waterdelivery.app.presentation.contact_picker

import androidx.compose.runtime.Composable

@Composable
actual fun RequestContactPermission(
    onResult: (Boolean) -> Unit
) {
    onResult(false)
}

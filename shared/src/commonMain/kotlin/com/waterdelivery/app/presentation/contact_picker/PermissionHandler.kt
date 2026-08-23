package com.waterdelivery.app.presentation.contact_picker

import androidx.compose.runtime.Composable

@Composable
expect fun RequestContactPermission(
    onResult: (Boolean) -> Unit
)

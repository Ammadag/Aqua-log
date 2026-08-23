package com.waterdelivery.app.presentation.contact_picker

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect

@Composable
actual fun RequestContactPermission(
    onResult: (Boolean) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = onResult
    )

    SideEffect {
        launcher.launch(Manifest.permission.READ_CONTACTS)
    }
}

package com.waterdelivery.app.core.platform

import androidx.compose.runtime.Composable

@Composable
actual fun rememberImagePicker(onImageSelected: (String) -> Unit): () -> Unit {
    // iOS implementation placeholder
    return { }
}

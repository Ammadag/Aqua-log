package com.waterdelivery.app.core.platform

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePicker(onImageSelected: (String) -> Unit): () -> Unit

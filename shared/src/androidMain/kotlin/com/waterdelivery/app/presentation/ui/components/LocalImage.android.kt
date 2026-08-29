package com.waterdelivery.app.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import android.graphics.BitmapFactory
import java.io.File

@Composable
actual fun LocalImage(
    path: String,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale
) {
    val bitmap = remember(path) {
        val file = File(path)
        if (file.exists()) {
            BitmapFactory.decodeFile(path)
        } else {
            null
        }
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}

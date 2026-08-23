package org.example.project

import androidx.compose.runtime.Composable
import com.waterdelivery.app.presentation.navigation.AppNavHost
import com.waterdelivery.app.presentation.ui.theme.WaterDeliveryTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    WaterDeliveryTheme {
        AppNavHost()
    }
}

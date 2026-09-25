package com.lumopos.display.screen.compose.app

import androidx.compose.runtime.remember
import com.lumopos.display.discovery.DisplayAdvertiser

@androidx.compose.runtime.Composable
actual fun rememberDisplayAdvertiser(
    appName: String,
    appAuthor: String,
    serviceType: String
): DisplayAdvertiser {
    return remember {
        DisplayAdvertiser(
            appName = appName,
            appAuthor = appAuthor,
            serviceType = serviceType
        )
    }
}
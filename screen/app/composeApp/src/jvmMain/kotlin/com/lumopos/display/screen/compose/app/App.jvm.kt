package com.lumopos.display.screen.compose.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.lumopos.display.discovery.DisplayAdvertiser

@Composable
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
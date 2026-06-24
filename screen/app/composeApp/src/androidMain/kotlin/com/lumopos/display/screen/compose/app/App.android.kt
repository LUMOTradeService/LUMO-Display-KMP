package com.lumopos.display.screen.compose.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.lumopos.display.discovery.DisplayAdvertiser

@Composable
actual fun rememberDisplayAdvertiser(
    appName: String,
    appAuthor: String,
    serviceType: String
): DisplayAdvertiser {
    val context = LocalContext.current
    return remember {
        DisplayAdvertiser(
            context = context,
            appName = appName,
            appAuthor = appAuthor,
            serviceType = serviceType
        )
    }
}
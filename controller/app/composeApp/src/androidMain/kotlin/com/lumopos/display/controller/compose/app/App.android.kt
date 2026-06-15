package com.lumopos.display.controller.compose.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.lumopos.display.discovery.DisplayDiscovery

@Composable
actual fun rememberDisplayDiscovery(): DisplayDiscovery {
    val context = LocalContext.current

    return remember {
        DisplayDiscovery(context)
    }
}
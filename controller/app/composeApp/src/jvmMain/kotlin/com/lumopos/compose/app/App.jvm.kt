package com.lumopos.compose.app

import androidx.compose.runtime.remember
import com.lumopos.display.discovery.DisplayDiscovery

@androidx.compose.runtime.Composable
actual fun rememberDisplayDiscovery(): com.lumopos.display.discovery.DisplayDiscovery {
    return remember { DisplayDiscovery() }
}
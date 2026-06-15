package com.lumopos.display.controller.compose.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.lumopos.display.discovery.DisplayDiscovery

@Composable
actual fun rememberDisplayDiscovery(): DisplayDiscovery {
    return remember { DisplayDiscovery() }
}
package com.lumopos.display.controller.compose.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.lumopos.display.controller.ScreenDiscovery
import com.lumopos.display.discovery.DisplayDiscovery

@Composable
actual fun rememberDisplayDiscovery(): ScreenDiscovery {
    return remember {
        ScreenDiscovery()
    }
}
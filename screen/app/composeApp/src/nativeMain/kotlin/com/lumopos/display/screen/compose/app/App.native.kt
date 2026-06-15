package com.lumopos.display.screen.compose.app

import androidx.compose.runtime.LaunchedEffect

@androidx.compose.runtime.Composable
actual fun LaunchDiscovery() {
    LaunchedEffect(Unit) {
        val discovery = com.lumopos.display.discovery.DisplayAdvertiser(
            appName = "LUMO Display",
            appAuthor = "LUMO trade service s.r.o.",
            serviceType = "_display._tcp"
        )
        discovery.advertise()
    }
}
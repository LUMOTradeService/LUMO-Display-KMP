package com.lumopos.display.screen.compose.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun LaunchDiscovery() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val discovery = com.lumopos.display.discovery.DisplayAdvertiser(
            context = context,
            appName = "LUMO Display",
            appAuthor = "LUMO trade service s.r.o.",
            serviceType = "_display._tcp"
        )
        discovery.advertise()
    }
}
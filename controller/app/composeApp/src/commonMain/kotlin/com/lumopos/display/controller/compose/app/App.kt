package com.lumopos.display.controller.compose.app

import androidx.compose.runtime.Composable
import com.lumopos.display.android.compose.theme.AppTheme
import com.lumopos.display.controller.compose.DisplaysScreen
import com.lumopos.display.controller.compose.state.DisplaysScreenState
import com.lumopos.display.controller.compose.viewModel.rememberDisplaysScreenViewModel
import com.lumopos.display.discovery.DisplayDiscovery

@Composable
expect fun rememberDisplayDiscovery(): DisplayDiscovery

@Composable
fun App() {
    val discovery = rememberDisplayDiscovery()
    val displaysScreenViewModel = rememberDisplaysScreenViewModel(
        discovery = discovery,
        serviceType = "_display._tcp"
    )

    AppTheme {
        DisplaysScreen(
            state = DisplaysScreenState(
                discoveredDisplays = displaysScreenViewModel.discoveredDisplays
            )
        )
    }
}


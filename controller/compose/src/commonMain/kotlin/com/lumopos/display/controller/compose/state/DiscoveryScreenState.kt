package com.lumopos.display.controller.compose.state

import com.lumopos.display.data.model.Screen
import kotlinx.coroutines.flow.StateFlow

data class DiscoveryScreenState(
    val discoveredDisplays: StateFlow<List<Screen>>
) {

}
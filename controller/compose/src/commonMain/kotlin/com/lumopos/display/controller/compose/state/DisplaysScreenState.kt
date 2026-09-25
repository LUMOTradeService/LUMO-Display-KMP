package com.lumopos.display.controller.compose.state

import com.lumopos.display.data.model.Display
import kotlinx.coroutines.flow.StateFlow

data class DisplaysScreenState(
    val discoveredDisplays: StateFlow<List<Display>>
) {

}
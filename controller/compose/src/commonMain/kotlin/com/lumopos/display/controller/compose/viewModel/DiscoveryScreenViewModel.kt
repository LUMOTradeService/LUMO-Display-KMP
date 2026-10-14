package com.lumopos.display.controller.compose.viewModel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lumopos.display.controller.ScreenDiscovery
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class DiscoveryScreenViewModel(
    private val discovery: ScreenDiscovery,
    private val serviceType: String
): ViewModel() {
    val discoveredDisplays = discovery.screens(serviceType).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()

    )

    init {
        discovery.startDiscovering(serviceType)
    }
}

@Composable
fun rememberDiscoveryScreenViewModel(
    discovery: ScreenDiscovery,
    serviceType: String
): DiscoveryScreenViewModel {
    return viewModel {
        DiscoveryScreenViewModel(
            discovery = discovery,
            serviceType = serviceType
        )
    }
}
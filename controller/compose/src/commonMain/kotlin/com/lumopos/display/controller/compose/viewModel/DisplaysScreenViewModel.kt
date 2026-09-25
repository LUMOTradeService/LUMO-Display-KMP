package com.lumopos.display.controller.compose.viewModel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lumopos.display.discovery.DisplayDiscovery
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class DisplaysScreenViewModel(
    private val discovery: DisplayDiscovery,
    private val serviceType: String
): ViewModel() {
    val discoveredDisplays = discovery.discover(serviceType).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()

    )
}

@Composable
fun rememberDisplaysScreenViewModel(
    discovery: DisplayDiscovery,
    serviceType: String
): DisplaysScreenViewModel {
    return viewModel {
        DisplaysScreenViewModel(
            discovery = discovery,
            serviceType = serviceType
        )
    }
}
package com.lumopos.display.screen.compose.viewModel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lumopos.display.discovery.DisplayAdvertiser
import com.lumopos.display.screen.compose.state.SetupScreenState
import kotlinx.coroutines.launch

class SetupScreenViewModel(
    displayAdvertiser: DisplayAdvertiser
): ViewModel() {
    val setupScreenState = SetupScreenState(
        display = displayAdvertiser.display
    )

    init {
        viewModelScope.launch {
            displayAdvertiser.advertise()
        }
    }
}

@Composable
fun setupScreenViewModel(
    displayAdvertiser: DisplayAdvertiser
): SetupScreenViewModel {
    return viewModel {
        SetupScreenViewModel(
            displayAdvertiser = displayAdvertiser
        )
    }
}
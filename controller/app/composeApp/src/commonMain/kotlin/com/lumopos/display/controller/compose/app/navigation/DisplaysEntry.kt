package com.lumopos.display.controller.compose.app.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.compose.dropUnlessStarted
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.lumopos.display.controller.compose.DisplaysScreen
import com.lumopos.display.controller.compose.app.ControllerNavKey
import com.lumopos.display.controller.compose.app.DisplaysNavKey
import com.lumopos.display.controller.compose.state.DisplaysScreenState
import com.lumopos.display.controller.compose.viewModel.rememberDisplaysScreenViewModel
import com.lumopos.display.discovery.DisplayDiscovery
import lumodisplay.controller.app.composeapp.generated.resources.Res
import lumodisplay.controller.app.composeapp.generated.resources.arrow_back
import lumodisplay.controller.app.composeapp.generated.resources.request_back
import lumodisplay.controller.app.composeapp.generated.resources.title_displays
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun EntryProviderScope<NavKey>.displaysEntry(
    appBackStack: NavBackStack<NavKey>,
    discovery: DisplayDiscovery,
    serviceType: String = "_display._tcp"
) {
    entry<DisplaysNavKey> {
        val displaysScreenViewModel = rememberDisplaysScreenViewModel(
            discovery = discovery,
            serviceType = serviceType
        )

        DisplaysScreen(
            state = DisplaysScreenState(
                discoveredDisplays = displaysScreenViewModel.discoveredDisplays
            ),
            navigationIcon = {
                IconButton(
                    onClick = dropUnlessStarted {
                        appBackStack.removeLastOrNull()
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_back),
                        contentDescription = stringResource(Res.string.request_back)
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(Res.string.title_displays)
                )
            }
        )
    }
}
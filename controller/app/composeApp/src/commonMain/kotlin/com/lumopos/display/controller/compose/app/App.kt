package com.lumopos.display.controller.compose.app

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.lumopos.display.controller.compose.app.navigation.counterEntry
import com.lumopos.display.controller.compose.app.navigation.displaysEntry
import com.lumopos.display.controller.compose.app.theme.AppTheme
import com.lumopos.display.discovery.DisplayDiscovery
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
internal sealed interface ControllerNavKey: NavKey

@Serializable
internal data object CounterNavKey: ControllerNavKey

@Serializable
internal data object DisplaysNavKey: ControllerNavKey
@OptIn(ExperimentalSerializationApi::class)
internal val controllerSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclassesOfSealed<ControllerNavKey>()
        }
    }
}

@Composable
expect fun rememberDisplayDiscovery(): DisplayDiscovery

@Composable
fun App() {
    val discovery = rememberDisplayDiscovery()
    val appBackStack = rememberNavBackStack(controllerSavedStateConfiguration, CounterNavKey)

    AppTheme {
        NavDisplay(
            backStack = appBackStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                counterEntry(appBackStack)
                displaysEntry(appBackStack, discovery)
            }
        )
    }
}
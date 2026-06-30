package com.lumopos.display.controller.compose.app

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.lumopos.display.controller.compose.DisplaysScreen
import com.lumopos.display.controller.compose.app.navigation.counterEntry
import com.lumopos.display.controller.compose.app.navigation.displaysEntry
import com.lumopos.display.controller.compose.app.theme.AppTheme
import com.lumopos.display.controller.compose.state.DisplaysScreenState
import com.lumopos.display.controller.compose.viewModel.rememberDisplaysScreenViewModel
import com.lumopos.display.discovery.DisplayDiscovery
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import lumodisplay.controller.app.composeapp.generated.resources.Res
import lumodisplay.controller.app.composeapp.generated.resources.arrow_back
import org.jetbrains.compose.resources.painterResource
import kotlin.collections.listOf

@Serializable
internal sealed interface ControllerNavKey: NavKey

@Serializable
data object DisplaysNavKey: NavKey
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
package com.lumopos.display.screen.compose.app

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.lumopos.display.discovery.DisplayAdvertiser
import com.lumopos.display.screen.compose.SetupScreen
import com.lumopos.display.screen.compose.viewModel.setupScreenViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
internal data object DisplayNavKey: NavKey {
    @Serializable
    data object Setup: NavKey

    @Serializable
    data object App: NavKey {
    }
}

internal val displaySavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(DisplayNavKey.Setup::class, DisplayNavKey.Setup.serializer())
            subclass(DisplayNavKey.App::class, DisplayNavKey.App.serializer())
        }
    }
}
@Composable
expect fun rememberDisplayAdvertiser(
    appName: String,
    appAuthor: String,
    serviceType: String
): DisplayAdvertiser

@Composable
fun App() {
    val displayAdvertiser = rememberDisplayAdvertiser(
        appName = "LUMO Display",
        appAuthor = "LUMO trade service s.r.o.",
        serviceType = "_display._tcp."
    )

    val setupScreenViewModel = setupScreenViewModel(
        displayAdvertiser = displayAdvertiser
    )

    Surface {
        NavDisplay(
            backStack = rememberNavBackStack(
                displaySavedStateConfiguration,
                DisplayNavKey.Setup
            ),
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<DisplayNavKey.Setup> {
                    val setupScreenViewModel = setupScreenViewModel(
                        displayAdvertiser = displayAdvertiser
                    )

                    SetupScreen(
                        state = setupScreenViewModel.setupScreenState
                    )
                }
            }
        )
    }
}
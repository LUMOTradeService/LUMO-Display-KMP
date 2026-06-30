package com.lumopos.display.controller.compose.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberSupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import lumodisplay.controller.app.composeapp.generated.resources.Res
import lumodisplay.controller.app.composeapp.generated.resources.app_name
import lumodisplay.controller.app.composeapp.generated.resources.settings
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Serializable
internal data object CounterNavKey: ControllerNavKey {
    @Serializable
    data object Main: NavKey

    @Serializable
    data object Supporting: NavKey
}

internal val counterSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(CounterNavKey.Main::class, CounterNavKey.Main.serializer())
            subclass(CounterNavKey.Supporting::class, CounterNavKey.Supporting.serializer())
        }
    }
}

@Composable
fun Main(
    requestSettings: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.app_name)
                    )
                },
                actions = {
                    IconButton(
                        onClick = requestSettings
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.settings),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.padding(paddingValues)
        ) {

        }
    }
}

@Composable
fun Supporting() {
    Scaffold { paddingValues ->
        Surface(
            modifier = Modifier.padding(paddingValues)
        ) {
            Text(
                text = "Supporting"
            )
        }
    }
}

@Composable
fun CounterScreen(
    requestSettings: () -> Unit = {}
) {
    val supportingPaneSceneStrategy = rememberSupportingPaneSceneStrategy<NavKey>()

    NavDisplay(
        backStack = rememberNavBackStack(counterSavedStateConfiguration, CounterNavKey.Main, CounterNavKey.Supporting),
        sceneStrategies = listOf(
            supportingPaneSceneStrategy
        ),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<CounterNavKey.Main>(
                metadata = SupportingPaneSceneStrategy.mainPane()
            ) {
                Main(
                    requestSettings = requestSettings
                )
            }
            entry<CounterNavKey.Supporting>(
                metadata = SupportingPaneSceneStrategy.supportingPane()
            ) {
                Supporting()
            }
        }
    )
}
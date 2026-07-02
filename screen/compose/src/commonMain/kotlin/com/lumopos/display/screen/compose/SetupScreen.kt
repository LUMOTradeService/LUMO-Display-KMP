package com.lumopos.display.screen.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.SupportingPaneSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberSupportingPaneSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import androidx.window.core.layout.WindowSizeClass
import com.lumopos.display.compose.aboutDisplayItems
import com.lumopos.display.compose.additionalContent
import com.lumopos.display.data.model.Display
import com.lumopos.display.screen.compose.state.SetupScreenState
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import lumodisplay.screen.compose.generated.resources.Res
import lumodisplay.screen.compose.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

@Serializable
private sealed interface SetupNavKey: NavKey
@Serializable
private data object SetupMainNavKey: SetupNavKey
@Serializable
private data object SetupSupportingNavKey: SetupNavKey

@OptIn(ExperimentalSerializationApi::class)
private val SetupSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclassesOfSealed<SetupNavKey>()
        }
    }
}

@Composable
private fun SetupTopBar() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(Res.string.app_name),
            )
        }
    )
}

@Composable
private fun CompactSetupScreen(
    state: SetupScreenState
) {
    Scaffold(
        topBar = {
            SetupTopBar()
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 8.dp,
                    end = 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                additionalContent()
                item(
                    key = "Spacer"
                ) {
                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )
                }
                aboutDisplayItems(
                    display = state.display
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ExpandedSetupScreen(
    state: SetupScreenState
) {
    val supportingPaneSceneStrategy = rememberSupportingPaneSceneStrategy<NavKey>(
        backNavigationBehavior = BackNavigationBehavior.PopUntilScaffoldValueChange
    )

    NavDisplay(
        backStack = rememberNavBackStack(SetupSavedStateConfiguration, SetupMainNavKey,
            SetupSupportingNavKey),
        sceneStrategies = listOf(
            supportingPaneSceneStrategy
        ),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<SetupMainNavKey>(
                metadata = SupportingPaneSceneStrategy.mainPane()
            ) {
                Scaffold(
                    topBar = {
                        SetupTopBar()
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        LazyColumn(
                            modifier = Modifier.padding(
                                top = 16.dp,
                                start = 8.dp,
                                end = 8.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            additionalContent()
                        }
                    }
                }
            }
            entry<SetupSupportingNavKey>(
                metadata = SupportingPaneSceneStrategy.supportingPane()
            ) {
                Scaffold { innerPadding ->
                    Surface(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        LazyColumn(
                            modifier = Modifier.padding(
                                top = 16.dp,
                                start = 8.dp,
                                end = 8.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            aboutDisplayItems(
                                display = state.display
                            )
                        }
                    }
                }
            }
        },
    )
}

@Composable
fun SetupScreen(
    state: SetupScreenState
) {
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass

    when {
        windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
        ) -> {
            ExpandedSetupScreen(
                state = state
            )
        }

        else -> {
            CompactSetupScreen(
                state = state
            )
        }
    }
}

@PreviewScreenSizes
@Composable
fun SetupScreenPreview() {
    SetupScreen(
        state = SetupScreenState(
            display = Display(
                appName = stringResource(Res.string.app_name),
                appAuthor = "LUMO trade service s.r.o.",
                version = "1.0.0",
                serviceType = "_display._tcp.",
                ipAddress = "192.168.1.100",
                port = 8080
            )
        )
    )
}
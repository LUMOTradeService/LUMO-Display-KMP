package com.lumopos.display.controller.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import com.lumopos.display.controller.compose.state.DisplaysScreenState
import com.lumopos.display.data.model.Display
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import lumodisplay.controller.compose.generated.resources.Res
import lumodisplay.controller.compose.generated.resources.monitor
import lumodisplay.controller.compose.generated.resources.supporting_displays_list_item
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.collections.listOf


@Serializable
internal data object DisplaysNavKey: NavKey {
    @Serializable
    data object List: NavKey

    @Serializable
    data class Detail(
        val display: Display
    ): NavKey {
    }
}

internal val DisplaysSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(DisplaysNavKey.List::class, DisplaysNavKey.List.serializer())
            subclass(DisplaysNavKey.Detail::class, DisplaysNavKey.Detail.serializer())
        }
    }
}

@Composable
private fun DisplaysListPane(
    availableDisplays: List<Display>
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Displays"
                    )
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.padding(paddingValues)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
            ) {
                items(
                    count = availableDisplays.size,
                    key = { index ->
                        availableDisplays[index].id.toString()
                    }
                ) { index ->
                    availableDisplays[index].let { display ->
                        SegmentedListItem(
                            onClick = {},
                            shapes = ListItemDefaults.segmentedShapes(
                                index = index,
                                count = availableDisplays.size
                            ),
                            colors = ListItemDefaults.segmentedColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                            ),
                            leadingContent = {
                                Icon(
                                    painter = painterResource(Res.drawable.monitor),
                                    contentDescription = null
                                )
                            },
                            content = {
                                Text(
                                    text = display.deviceName
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = stringResource(
                                        Res.string.supporting_displays_list_item,
                                        display.ipAddress,
                                        display.port
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DisplaysDetailPane() {
    Text(
        text = "Display detail"
    )
}

@Composable
fun DisplaysDetailPanePlaceholder() {
    Text(
        text = "Placeholder"
    )
}

@Composable
fun DisplaysScreen(
    state: DisplaysScreenState
) {
    val listDetailSceneStrategy = rememberListDetailSceneStrategy<NavKey>()
    val discoveredDisplays by state.discoveredDisplays.collectAsStateWithLifecycle()

    Scaffold { paddingValues ->
        Surface(
            modifier = Modifier.padding(paddingValues)
        ) {
            DisplaysListPane(discoveredDisplays)
        }
    }

    NavDisplay(
        backStack = rememberNavBackStack(DisplaysSavedStateConfiguration, DisplaysNavKey.List),
        sceneStrategies = listOf(
            listDetailSceneStrategy
        ),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<DisplaysNavKey.List>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = {
                        DisplaysDetailPanePlaceholder()
                    }
                )
            ) {
                DisplaysListPane(discoveredDisplays)
            }
            entry<DisplaysNavKey.Detail>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { /* detail -> */
                DisplaysDetailPane()
            }
        }
    )
}

@Preview(
    showBackground = true
)
@Composable
fun DisplaysListPreview() {
    DisplaysScreen(
        state = DisplaysScreenState(
            MutableStateFlow(
                listOf(
                    Display(
                        deviceName = "Device name",
                        appName = "LUMO Display",
                        appAuthor = "LUMO trade service",
                        version = "1.0.0",
                        serviceType = "Display",
                        ipAddress = "192.168.0.10",
                        port = 8080
                    ),
                    Display(
                        deviceName = "Device name",
                        appName = "LUMO Display",
                        appAuthor = "LUMO trade service",
                        version = "1.0.0",
                        serviceType = "Display",
                        ipAddress = "192.168.0.10",
                        port = 8080
                    ),
                    Display(
                        deviceName = "Device name",
                        appName = "LUMO Display",
                        appAuthor = "LUMO trade service",
                        version = "1.0.0",
                        serviceType = "Display",
                        ipAddress = "192.168.0.10",
                        port = 8080
                    ),
                    Display(
                        deviceName = "Device name",
                        appName = "LUMO Display",
                        appAuthor = "LUMO trade service",
                        version = "1.0.0",
                        serviceType = "Display",
                        ipAddress = "192.168.0.10",
                        port = 8080
                    )
                )
            )
        )
    )
}
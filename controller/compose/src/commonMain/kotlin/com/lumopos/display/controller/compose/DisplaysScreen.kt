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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import com.lumopos.display.controller.compose.state.DisplaysScreenState
import com.lumopos.display.data.model.Display
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import lumodisplay.controller.compose.generated.resources.Res
import lumodisplay.controller.compose.generated.resources.monitor
import lumodisplay.controller.compose.generated.resources.supporting_displays_list_item
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
/*
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

internal val listDetailConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(DisplaysNavKey.List::class, DisplaysNavKey.List.serializer())
            subclass(DisplaysNavKey.Detail::class, DisplaysNavKey.Detail.serializer())
        }
    }
}*/

@Composable
fun DisplaysScreen(
    state: DisplaysScreenState
) {
    val discoveredDisplays by state.discoveredDisplays.collectAsStateWithLifecycle()

    Scaffold { paddingValues ->
        Surface(
            modifier = Modifier.padding(paddingValues)
        ) {
            DisplaysList(discoveredDisplays)
        }
    }

}

@Composable
private fun DisplaysList(
    availableDisplays: List<Display>
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

@Preview(
    showBackground = true
)
@Composable
fun DisplaysListPreview() {
    DisplaysList(
        availableDisplays = listOf(
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
}
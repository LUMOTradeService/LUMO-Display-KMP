package com.lumopos.display.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lumopos.display.data.model.Display
import lumodisplay.compose.generated.resources.Res
import lumodisplay.compose.generated.resources.title_about_device_and_app
import lumodisplay.compose.generated.resources.title_app_name
import lumodisplay.compose.generated.resources.title_author
import lumodisplay.compose.generated.resources.title_device_name
import lumodisplay.compose.generated.resources.title_ip_address
import lumodisplay.compose.generated.resources.title_version
import org.jetbrains.compose.resources.stringResource


fun LazyListScope.aboutDisplayItems(
    display: Display
) {
    item(
        key = "AboutDeviceAndAppTitle"
    ) {
        Text(
            text = stringResource(Res.string.title_about_device_and_app),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        )
    }
    item(
        key = "DeviceName"
    ) {
        AboutDisplayItem(
            headline = stringResource(Res.string.title_device_name),
            supporting = display.deviceName
        )
    }
    item(
        key = "DeviceIpAddress"
    ) {
        AboutDisplayItem(
            headline = stringResource(Res.string.title_ip_address),
            supporting = display.ipAddress
        )
    }
    item(
        key = "AppName"
    ) {
        AboutDisplayItem(
            headline = stringResource(Res.string.title_app_name),
            supporting = display.appName
        )
    }
    item(
        key = "AppVersion"
    ) {
        AboutDisplayItem(
            headline = stringResource(Res.string.title_version),
            supporting = display.version
        )
    }
    item(
        key = "AppAuthor"
    ) {
        AboutDisplayItem(
            headline = stringResource(Res.string.title_author),
            supporting = display.appAuthor
        )
    }
}

@Composable
private fun AboutDisplayItem(
    headline: String,
    supporting: String
) {
    ListItem(
        onClick = {},
        content = {
            Text(
                text = headline
            )
        },
        supportingContent = {
            Text(
                text = supporting
            )
        }
    )
}

@Composable
fun DisplayInfoScreen(
    display: Display,
    topBar: @Composable () -> Unit = {},
    content: LazyListScope.() -> Unit = {}
) {
    Scaffold(
        topBar = topBar
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                content()
                item(
                    key = "Spacer"
                ) {
                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )
                }
                aboutDisplayItems(
                    display = display
                )
            }
        }
    }
}
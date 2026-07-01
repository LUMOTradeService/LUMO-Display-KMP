package com.lumopos.display.controller.compose.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import lumodisplay.controller.app.composeapp.generated.resources.Res
import lumodisplay.controller.app.composeapp.generated.resources.add
import lumodisplay.controller.app.composeapp.generated.resources.app_name
import lumodisplay.controller.app.composeapp.generated.resources.monitor
import lumodisplay.controller.app.composeapp.generated.resources.remove
import lumodisplay.controller.app.composeapp.generated.resources.settings
import lumodisplay.controller.app.composeapp.generated.resources.title_counter
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DisplayListItem() {
    ListItem(
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.monitor),
                contentDescription = null
            )
        },
        headlineContent = {
            Text(
                text = "LUMO Display"
            )
        },
        trailingContent = {
            Text(
                text = "4"
            )
        }
    )
}

@Composable
private fun RowScope.MediumButton(
    icon: Painter,
    text: String
) {
    Button(
        onClick = {},
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        modifier = Modifier.weight(1f)
            .heightIn(ButtonDefaults.MediumContainerHeight)
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.MediumIconSize)
        )

        Spacer(modifier = Modifier.size(ButtonDefaults.MediumIconSpacing))

        Text(
            text = text
        )
    }
}

@Composable
fun CounterScreen(
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
        },
        bottomBar = {
            Surface(
                modifier = Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 16.dp
                )
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        MediumButton(
                            icon = painterResource(Res.drawable.add),
                            text = "Add"
                        )
                        MediumButton(
                            icon = painterResource(Res.drawable.remove),
                            text = "Remove"
                        )
                    }
                    val size = ButtonDefaults.LargeContainerHeight
                    Button(
                        onClick = {},
                        contentPadding = ButtonDefaults.contentPaddingFor(size, hasStartIcon = true),
                        modifier = Modifier.fillMaxWidth()
                            .heightIn(size)
                    ) {
                        Spacer(Modifier.size(ButtonDefaults.LargeIconSpacing))
                        Text(
                            text = "Reset",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.padding(paddingValues)
                .padding(
                    horizontal = 8.dp
                )
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(Res.string.title_counter),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "0",
                            style = MaterialTheme.typography.displayLarge
                        )
                    }
                }
                Column {
                    Surface(
                        modifier = Modifier.padding(
                            vertical = 10.dp,
                            horizontal = 16.dp
                        )
                    ) {
                        Text(
                            text = "Connected displays",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    LazyColumn {
                        items(20) {
                            DisplayListItem()
                        }
                    }
                }
            }
        }
    }
}
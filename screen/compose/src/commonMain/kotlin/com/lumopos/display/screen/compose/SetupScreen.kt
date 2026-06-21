package com.lumopos.display.screen.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lumopos.display.compose.aboutDisplayItems
import com.lumopos.display.compose.additionalContent
import com.lumopos.display.data.model.Display
import com.lumopos.display.screen.compose.state.SetupScreenState
import lumodisplay.screen.compose.generated.resources.Res
import lumodisplay.screen.compose.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

@Composable
fun SetupTopBar() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(Res.string.app_name),
            )
        }
    )
}

@Composable
fun SetupScreen(
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
                    start = 16.dp,
                    end = 16.dp
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

@Preview
@Composable
fun SetupScreenPreview() {
    SetupScreen(
        state = SetupScreenState(
            display = Display(
                appName = "LUMO Display",
                appAuthor = "LUMO trade service s.r.o.",
                version = "1.0.0",
                serviceType = "_display._tcp.",
                ipAddress = "192.168.1.100",
                port = 8080
            )
        )
    )
}
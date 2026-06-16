package com.lumopos.display.screen.compose

import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import com.lumopos.display.compose.DisplayInfoScreen
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
    DisplayInfoScreen(
        display = state.display,
        topBar = {
            SetupTopBar()
        }
    )
}
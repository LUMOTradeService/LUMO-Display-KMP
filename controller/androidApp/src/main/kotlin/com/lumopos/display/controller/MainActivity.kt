package com.lumopos.display.controller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.lumopos.display.controller.compose.DisplaysScreen
import com.lumopos.display.controller.compose.state.DisplaysScreenState
import com.lumopos.display.controller.compose.viewModel.DisplaysScreenViewModel
import com.lumopos.display.controller.compose.viewModel.rememberDisplaysScreenViewModel
import com.lumopos.display.discovery.DisplayDiscovery

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current

            val discovery = remember {
                DisplayDiscovery(context)
            }
            val displaysScreenViewModel = rememberDisplaysScreenViewModel(
                discovery = discovery,
                serviceType = "_display._tcp"
            )
            DisplaysScreen(
                state = DisplaysScreenState(
                    discoveredDisplays = displaysScreenViewModel.discoveredDisplays
                )
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
}
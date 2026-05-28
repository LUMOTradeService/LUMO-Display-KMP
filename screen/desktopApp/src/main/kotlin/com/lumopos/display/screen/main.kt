package com.lumopos.display.screen

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lumopos.display.screen.compose.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "LUMODisplayScreen",
    ) {
        App()
    }
}
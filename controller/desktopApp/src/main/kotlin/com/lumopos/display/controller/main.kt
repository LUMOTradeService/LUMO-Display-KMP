package com.lumopos.display.controller

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lumopos.display.controller.compose.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "LUMODisplayController",
    ) {
        App()
    }
}
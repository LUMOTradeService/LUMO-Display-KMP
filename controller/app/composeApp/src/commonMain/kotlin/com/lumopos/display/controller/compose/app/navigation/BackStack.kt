package com.lumopos.display.controller.compose.app.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

internal fun <T: NavKey> NavBackStack<T>.pushDistinct(key: T) {
    if (this.lastOrNull() != key) {
        this.add(key)
    }
}
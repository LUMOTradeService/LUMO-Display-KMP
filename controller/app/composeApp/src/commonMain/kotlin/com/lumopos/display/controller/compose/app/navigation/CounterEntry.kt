package com.lumopos.display.controller.compose.app.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.lumopos.display.controller.compose.app.CounterNavKey
import com.lumopos.display.controller.compose.app.CounterScreen
import com.lumopos.display.controller.compose.app.DisplaysNavKey

fun EntryProviderScope<NavKey>.counterEntry(
    appBackStack: NavBackStack<NavKey>
) {
    entry<CounterNavKey> {
        CounterScreen(
            requestSettings = {
                appBackStack.pushDistinct(DisplaysNavKey)
            }
        )
    }
}
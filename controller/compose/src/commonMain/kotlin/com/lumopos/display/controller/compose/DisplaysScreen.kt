package com.lumopos.display.controller.compose

import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import com.lumopos.display.data.model.Display
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
internal data object DisplaysDestination: NavKey {
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
            subclass(DisplaysDestination.List::class, DisplaysDestination.List.serializer())
            subclass(DisplaysDestination.Detail::class, DisplaysDestination.Detail.serializer())
        }
    }
}
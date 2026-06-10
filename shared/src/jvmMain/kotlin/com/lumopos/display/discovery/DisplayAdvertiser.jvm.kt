package com.lumopos.display.discovery

import com.lumopos.display.data.model.Display

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class DisplayAdvertiser {
    actual var display: Display
        get() = TODO("Not yet implemented")
        set(value) {}

    actual suspend fun advertise() {
    }

    actual fun stopAdvertising() {
    }
}
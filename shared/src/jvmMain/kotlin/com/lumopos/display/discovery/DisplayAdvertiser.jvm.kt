package com.lumopos.display.discovery

import com.lumopos.display.data.model.Display
import com.lumopos.display.data.model.currentAppVersion
import com.lumopos.display.data.model.getDeviceName

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class DisplayAdvertiser(
    appName: String,
    appAuthor: String,
    serviceType: String,
) {
    actual var display: Display = Display(
        deviceName = getDeviceName(),
        appName = appName,
        appAuthor = appAuthor,
        version = currentAppVersion(),
        serviceType = serviceType
    )
        private set

    actual suspend fun advertise() {
    }

    actual fun stopAdvertising() {
    }
}
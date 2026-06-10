package com.lumopos.display.discovery

import com.lumopos.display.data.model.Display
import kotlinx.coroutines.flow.Flow

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class DisplayDiscovery {
    actual fun discover(serviceType: String): Flow<List<Display>> {
        TODO("Not yet implemented")
    }

    actual fun pauseDiscovering(serviceType: String) {
    }

    actual fun restartDiscovering(serviceType: String) {
    }

    actual fun stopDiscovering(serviceType: String) {
    }
}
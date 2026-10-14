package com.lumopos.display.controller

import com.lumopos.display.data.model.Screen
import kotlinx.coroutines.flow.Flow

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class ScreenDiscovery {
    actual fun screens(serviceType: String): Flow<List<Screen>> {
        TODO("Not yet implemented")
    }

    actual fun startDiscovering(serviceType: String) {
    }

    actual fun pauseDiscovering(serviceType: String) {
    }

    actual fun stopDiscovering(serviceType: String) {
    }

    actual interface Listener {
        actual fun onServiceFound(screen: Screen)
        actual fun onServiceLost(screen: Screen)
        actual fun onDiscoveryStarted()
        actual fun onDiscoveryStopped()
        actual fun onDiscoveryFailed(errorCode: String)
    }
}
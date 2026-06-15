package com.lumopos.display.discovery

import com.lumopos.display.data.model.Display
import com.lumopos.display.discovery.extension.netServiceBrowserDelegate
import kotlinx.cinterop.BetaInteropApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.Foundation.NSNetServiceBrowser

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DisplayDiscovery() {
    private var browsers: MutableMap<String, NSNetServiceBrowser> = mutableMapOf()
    private val availableDisplays: MutableList<Display> = mutableListOf()


    @OptIn(BetaInteropApi::class)
    actual fun discover(serviceType: String): Flow<List<Display>> = callbackFlow {
        browsers[serviceType] = NSNetServiceBrowser().apply {
            delegate = netServiceBrowserDelegate(availableDisplays)

            searchForServicesOfType(
                serviceType,
                inDomain = "local."
            )
        }

        awaitClose {
            stopDiscovering(serviceType)
        }
    }

    actual fun pauseDiscovering(serviceType: String) {
        browsers[serviceType]?.stop()
    }

    @OptIn(BetaInteropApi::class)
    actual fun restartDiscovering(serviceType: String) {
        browsers[serviceType]?.searchForServicesOfType(
            serviceType,
            inDomain = "local."
        )
    }

    actual fun stopDiscovering(serviceType: String) {
        browsers[serviceType]?.stop()
        browsers.remove(serviceType)
    }
}
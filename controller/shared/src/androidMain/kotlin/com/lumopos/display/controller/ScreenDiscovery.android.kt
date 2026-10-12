package com.lumopos.display.controller

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import com.lumopos.display.data.model.Display
import com.lumopos.display.controller.extension.resolveFoundService
import com.lumopos.display.controller.extension.resolveLostService
import com.lumopos.display.controller.extension.clearDiscoveredScreens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class ScreenDiscovery(
    context: Context
) {
    private val nsdManager = context.getSystemService(
        Context.NSD_SERVICE
    ) as NsdManager
    private var discoveryListeners: MutableMap<String, NsdManager.DiscoveryListener> = mutableMapOf()
    private val discoveredScreens: MutableMap<String, MutableList<Display>> = mutableMapOf()

    actual fun discover(serviceType: String): Flow<List<Display>> = callbackFlow {
        val discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onServiceFound(service: NsdServiceInfo) {
                val displays = discoveredScreens.getOrPut(serviceType) { mutableListOf() }
                resolveFoundService(nsdManager, service, displays)
            }
            override fun onServiceLost(service: NsdServiceInfo) {
                discoveredScreens[serviceType]?.let {
                    resolveLostService(service, it)
                }
            }
            override fun onDiscoveryStarted(serviceType: String) {
                discoveredScreens[serviceType]?.let {
                    clearDiscoveredScreens(it)
                }
            }
            override fun onDiscoveryStopped(serviceType: String) {}
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                close(Exception("Start discovery failed with error code: $errorCode"))
            }
            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                close(Exception("Stop discovery failed with error code: $errorCode"))
            }
        }

        discoveryListeners[serviceType] = discoveryListener

        nsdManager.discoverServices(
            serviceType,
            NsdManager.PROTOCOL_DNS_SD,
            discoveryListener
        )

        awaitClose {
            stopDiscovering(serviceType)
        }
    }.flowOn(Dispatchers.IO)

    actual fun pauseDiscovering(serviceType: String) {
        discoveryListeners[serviceType]?.let {
            nsdManager.stopServiceDiscovery(it)
        }
    }

    actual fun restartDiscovering(serviceType: String) {
        discoveryListeners[serviceType]?.let { discoveryListener ->
            try {
                nsdManager.discoverServices(
                    serviceType,
                    NsdManager.PROTOCOL_DNS_SD,
                    discoveryListener
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    actual fun stopDiscovering(serviceType: String) {
        discoveryListeners[serviceType]?.let {
            nsdManager.stopServiceDiscovery(it)
        }
        discoveryListeners.remove(serviceType)
    }
}
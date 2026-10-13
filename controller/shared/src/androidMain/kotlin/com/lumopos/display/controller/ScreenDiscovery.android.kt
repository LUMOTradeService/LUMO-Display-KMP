package com.lumopos.display.controller

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.os.ext.SdkExtensions
import com.lumopos.display.controller.extension.appAuthor
import com.lumopos.display.controller.extension.appName
import com.lumopos.display.controller.extension.appVersion
import com.lumopos.display.controller.extension.id
import com.lumopos.display.controller.extension.ipAddress
import com.lumopos.display.data.model.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlin.uuid.Uuid

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class ScreenDiscovery(
    context: Context
) {
    private val nsdManager = context.getSystemService(
        Context.NSD_SERVICE
    ) as NsdManager
    private var discoveryListeners: MutableMap<String, NsdManager.DiscoveryListener> = mutableMapOf()
    private var screenDiscoveryListeners: MutableList<ScreenDiscoveryListener> = mutableListOf()
    private val discoveredScreens: MutableMap<String, MutableList<Screen>> = mutableMapOf()

    private fun foundScreen(
        service: NsdServiceInfo,
        discoveredScreens: MutableList<Screen>,
    ) {
        val screenId = service.id ?: Uuid.random()

        discoveredScreens.removeAll { it.serviceName == service.serviceName || it.id == screenId }
        discoveredScreens.add(
            Screen(
                id = screenId,
                appName = service.appName,
                appAuthor = service.appAuthor,
                version = service.appVersion,
                serviceName = service.serviceName,
                serviceType = service.serviceType,
                ipAddress = service.ipAddress,
                port = service.port
            )
        )
    }

    private fun resolveLegacy(
        nsdManager: NsdManager,
        service: NsdServiceInfo,
        discoveredScreens: MutableList<Screen>
    ) {
        @Suppress("DEPRECATION")
        nsdManager.resolveService(service, object : NsdManager.ResolveListener {
            override fun onServiceResolved(resolved: NsdServiceInfo) {
                foundScreen(
                    service = resolved,
                    discoveredScreens = discoveredScreens
                )
            }

            override fun onResolveFailed(service: NsdServiceInfo?, errorCode: Int) {
                throw RuntimeException("Resolve ${service?.serviceName} failed with error code: $errorCode")
            }
        })
    }

    private fun resolveFoundService(
        nsdManager: NsdManager,
        service: NsdServiceInfo
    ) {
        discoveredScreens[service.serviceType]?.let { screens ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && SdkExtensions.getExtensionVersion(
                    Build.VERSION_CODES.TIRAMISU
                ) >= 7
            ) {
                nsdManager.registerServiceInfoCallback(
                    service,
                    Runnable::run,
                    object : NsdManager.ServiceInfoCallback {
                        override fun onServiceUpdated(updated: NsdServiceInfo) {
                            foundScreen(
                                service = updated,
                                discoveredScreens = screens
                            )
                        }

                        override fun onServiceInfoCallbackRegistrationFailed(p0: Int) {
                            throw RuntimeException("Registration failed with error code: $p0")
                        }

                        override fun onServiceInfoCallbackUnregistered() {}
                        override fun onServiceLost() {
                            nsdManager.unregisterServiceInfoCallback(this)
                        }
                    })
            } else {
                resolveLegacy(nsdManager, service, screens)
            }
        }
    }

    private fun resolveLostService(
        service: NsdServiceInfo
    ) {
        discoveredScreens[service.serviceType]?.let { screens ->
            screenDiscoveryListeners.forEach {
                screens.find { screen -> screen.serviceName == service.serviceName }?.let { screen ->
                    screens.remove(screen)
                    it.onServiceLost(screen)
                }
            }
        }
    }

    actual fun screens(serviceType: String): Flow<List<Screen>> = callbackFlow {
        val listener = object : ScreenDiscoveryListener {
                private fun update() {
                    discoveredScreens[serviceType]?.let {
                        trySend(it.toList())
                    }
                }

                override fun onServiceFound(screen: Screen) {
                    update()
                }

                override fun onServiceLost(screen: Screen) {
                    update()
                }

                override fun onDiscoveryStarted() {
                    update()
                }

                override fun onDiscoveryStopped() {

                }

                override fun onStartDiscoveryFailed(errorCode: Int) {

                }

                override fun onStopDiscoveryFailed(errorCode: Int) {

                }

            }

        screenDiscoveryListeners.add(listener)

        awaitClose {
            stopDiscovering(serviceType)
            screenDiscoveryListeners.remove(listener)
        }
    }.flowOn(Dispatchers.IO)

    actual fun startDiscovering(serviceType: String) {
        val discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onServiceFound(service: NsdServiceInfo) {
                discoveredScreens.getOrPut(serviceType) { mutableListOf() }
                resolveFoundService(nsdManager, service)
            }
            override fun onServiceLost(service: NsdServiceInfo) {
                resolveLostService(service)
            }
            override fun onDiscoveryStarted(serviceType: String) {
                discoveredScreens[serviceType]?.clear()
            }
            override fun onDiscoveryStopped(serviceType: String) {

            }
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {

            }
            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {

            }
        }

        discoveryListeners[serviceType] = discoveryListener

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

    actual fun pauseDiscovering(serviceType: String) {
        discoveryListeners[serviceType]?.let {
            nsdManager.stopServiceDiscovery(it)
        }
    }

    actual fun stopDiscovering(serviceType: String) {
        discoveryListeners[serviceType]?.let {
            nsdManager.stopServiceDiscovery(it)
        }
        discoveryListeners.remove(serviceType)
    }

    actual interface ScreenDiscoveryListener {
        actual fun onServiceFound(screen: Screen)
        actual fun onServiceLost(screen: Screen)
        actual fun onDiscoveryStarted()
        actual fun onDiscoveryStopped()
        actual fun onStartDiscoveryFailed(errorCode: Int)
        actual fun onStopDiscoveryFailed(errorCode: Int)
    }
}
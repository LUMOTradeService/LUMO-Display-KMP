package com.lumopos.display.controller.extension

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.os.ext.SdkExtensions
import com.lumopos.display.data.model.Screen
import kotlinx.coroutines.channels.ProducerScope
import kotlin.uuid.Uuid

/**
 * Updates the list of [discoveredScreens] with information from the resolved [service]
 * and emits the updated list to the [ProducerScope].
 *
 * This function extracts the display ID and version from the service attributes,
 * replaces any existing entry with the same service name, and sends a new immutable
 * copy of the list to the flow.
 *
 * @param service The [NsdServiceInfo] containing the discovered service details.
 * @param discoveredScreens The mutable list tracking currently active displays.
 */
private fun ProducerScope<List<Screen>>.foundScreen(
    service: NsdServiceInfo,
    discoveredScreens: MutableList<Screen>,
) {
    synchronized(discoveredScreens) {
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
        trySend(
            discoveredScreens.toList()
        )
    }
}

/**
 * Resolves a discovered network service using the legacy [NsdManager.resolveService] API.
 *
 * This method is used for Android versions prior to Tiramisu (API 33) or where the newer
 * service info callbacks are not supported. Upon successful resolution, it extracts the
 * host address and updates the [discoveredScreens] list via [foundScreen].
 *
 * @param nsdManager The system service used to resolve the network service.
 * @param service The service discovered on the network that needs to be resolved.
 * @param discoveredScreens The mutable list of currently discovered customer displays to be updated.
 */
private fun ProducerScope<List<Screen>>.resolveLegacy(
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
            close(Exception("Resolve ${service?.serviceName} failed with error code: $errorCode"))
        }
    })
}

/**
 * Resolves network service information using the modern [NsdManager.ServiceInfoCallback] API
 * introduced in Android T (API 33).
 *
 * This function registers a callback to monitor a specific [NsdServiceInfo]. When the service
 * is updated with network details (like host addresses), it updates the [discoveredScreens]
 * list and emits the new state to the [ProducerScope].
 *
 * @param nsdManager The system service used to manage network service discovery.
 * @param service The discovered service that needs to be resolved.
 * @param discoveredScreens The mutable list of currently known customer displays to be updated.
 * @see [NsdManager.registerServiceInfoCallback]
 */
internal fun ProducerScope<List<Screen>>.resolveFoundService(
    nsdManager: NsdManager,
    service: NsdServiceInfo,
    discoveredScreens: MutableList<Screen>
) {
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
                        discoveredScreens = discoveredScreens
                    )
                }

                override fun onServiceInfoCallbackRegistrationFailed(p0: Int) {
                    close(Exception("Registration failed with error code: $p0"))
                }

                override fun onServiceInfoCallbackUnregistered() {}
                override fun onServiceLost() {
                    nsdManager.unregisterServiceInfoCallback(this)
                }
            })
    } else {
        resolveLegacy(nsdManager, service, discoveredScreens)
    }
}

/**
 * Handles the event when a network service is no longer available.
 *
 * This function removes the service from the list of discovered services and
 * emits the updated list to the flow.
 *
 * @param service The [NsdServiceInfo] representing the service that has been lost.
 * @param discoveredScreens The mutable list of currently discovered customer displays.
 */
internal fun ProducerScope<List<Screen>>.resolveLostService(
    service: NsdServiceInfo,
    discoveredScreens: MutableList<Screen>
) {
    synchronized(discoveredScreens) {
        discoveredScreens.removeAll { it.serviceName == service.serviceName }
        trySend(discoveredScreens.toList())
    }
}

/**
 * Handles the event when the service discovery process has started.
 *
 * This function clears the list of discovered services and emits the updated
 * list to the flow.
 *
 * @param discoveredScreens The mutable list of currently discovered customer displays.
 */
internal fun ProducerScope<List<Screen>>.clearDiscoveredScreens(
    discoveredScreens: MutableList<Screen>
) {
    synchronized(discoveredScreens) {
        discoveredScreens.clear()
        trySend(
            discoveredScreens.toList()
        )
    }
}
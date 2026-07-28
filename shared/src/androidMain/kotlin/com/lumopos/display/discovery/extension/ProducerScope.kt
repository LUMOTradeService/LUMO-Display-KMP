package com.lumopos.display.discovery.extension

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.os.ext.SdkExtensions
import com.lumopos.display.data.model.Display
import kotlinx.coroutines.channels.ProducerScope
import kotlin.uuid.Uuid

/**
 * Updates the list of [discoveredDisplays] with information from the resolved [service]
 * and emits the updated list to the [ProducerScope].
 *
 * This function extracts the display ID and version from the service attributes,
 * replaces any existing entry with the same service name, and sends a new immutable
 * copy of the list to the flow.
 *
 * @param service The [NsdServiceInfo] containing the discovered service details.
 * @param discoveredDisplays The mutable list tracking currently active displays.
 */
private fun ProducerScope<List<Display>>.foundDisplay(
    service: NsdServiceInfo,
    discoveredDisplays: MutableList<Display>,
) {
    val lock = Any()

    synchronized(lock) {
        val displayId = service.id ?: Uuid.random()
        val version = service.appVersion
        discoveredDisplays.removeAll { it.id == displayId }
        discoveredDisplays.add(
            Display(
                id = displayId,
                deviceName = service.serviceName,
                ipAddress = service.ipAddress,
                port = service.port,
                appName = service.appName,
                appAuthor = service.appAuthor,
                version = version,
                serviceType = service.serviceType
            )
        )
        trySend(
            discoveredDisplays.toList()
        )
    }
}

/**
 * Removes a display from the collection of discovered displays.
 *
 * This function updates the state by filtering out the specified display,
 * ensuring it is no longer advertised or available for pairing within the current scope.
 *
 * @param service The [NsdServiceInfo] representing the display to be removed.
 * @param discoveredDisplays The mutable list of currently available displays.
 */
private fun ProducerScope<List<Display>>.lostDisplay(
    service: NsdServiceInfo,
    discoveredDisplays: MutableList<Display>
) {
    val lock = Any()

    synchronized(lock) {
        discoveredDisplays.removeAll { it.deviceName == service.serviceName }
        trySend(discoveredDisplays.toList())
    }
}

/**
 * Resolves a discovered network service using the legacy [NsdManager.resolveService] API.
 *
 * This method is used for Android versions prior to Tiramisu (API 33) or where the newer
 * service info callbacks are not supported. Upon successful resolution, it extracts the
 * host address and updates the [discoveredDisplays] list via [foundDisplay].
 *
 * @param nsdManager The system service used to resolve the network service.
 * @param service The service discovered on the network that needs to be resolved.
 * @param discoveredDisplays The mutable list of currently discovered customer displays to be updated.
 */
private fun ProducerScope<List<Display>>.resolveLegacy(
    nsdManager: NsdManager,
    service: NsdServiceInfo,
    discoveredDisplays: MutableList<Display>
) {
    @Suppress("DEPRECATION")
    nsdManager.resolveService(service, object : NsdManager.ResolveListener {
        override fun onServiceResolved(resolved: NsdServiceInfo) {
            foundDisplay(
                service = resolved,
                discoveredDisplays = discoveredDisplays
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
 * is updated with network details (like host addresses), it updates the [discoveredDisplays]
 * list and emits the new state to the [ProducerScope].
 *
 * @param nsdManager The system service used to manage network service discovery.
 * @param service The discovered service that needs to be resolved.
 * @param discoveredDisplays The mutable list of currently known customer displays to be updated.
 */
internal fun ProducerScope<List<Display>>.foundResolve(
    nsdManager: NsdManager,
    service: NsdServiceInfo,
    discoveredDisplays: MutableList<Display>
) {
    if (SdkExtensions.getExtensionVersion(Build.VERSION_CODES.TIRAMISU) >= 7) {
        nsdManager.registerServiceInfoCallback(service, Runnable::run, object : NsdManager.ServiceInfoCallback {
            override fun onServiceUpdated(updated: NsdServiceInfo) {
                foundDisplay(
                    service = updated,
                    discoveredDisplays = discoveredDisplays
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
        resolveLegacy(nsdManager, service, discoveredDisplays)
    }
}

/**
 * Handles the event when a network service is no longer available.
 *
 */
internal fun ProducerScope<List<Display>>.lostResolve(
    service: NsdServiceInfo,
    discoveredDisplays: MutableList<Display>
) {
    lostDisplay(
        service = service,
        discoveredDisplays = discoveredDisplays
    )
}

internal fun ProducerScope<List<Display>>.startedResolve(
    discoveredDisplays: MutableList<Display>
) {
    val lock = Any()

    synchronized(lock) {
        discoveredDisplays.clear()
        trySend(
            discoveredDisplays.toList()
        )
    }
}
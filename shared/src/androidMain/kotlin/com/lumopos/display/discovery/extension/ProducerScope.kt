package com.lumopos.display.discovery.extension

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.os.ext.SdkExtensions
import com.lumopos.display.data.Display
import kotlinx.coroutines.channels.ProducerScope
import kotlin.uuid.Uuid

/**
 * Updates the list of [availableDisplays] with information from the resolved [service]
 * and emits the updated list to the [ProducerScope].
 *
 * This function extracts the display ID and version from the service attributes,
 * replaces any existing entry with the same service name, and sends a new immutable
 * copy of the list to the flow.
 *
 * @param service The [NsdServiceInfo] containing the discovered service details.
 * @param availableDisplays The mutable list tracking currently active displays.
 */
internal fun ProducerScope<List<Display>>.updateAvailableDisplays(
    service: NsdServiceInfo,
    availableDisplays: MutableList<Display>,
) {
    val lock = Any()

    synchronized(lock) {
        val displayId = service.id ?: Uuid.random()
        val version = service.appVersion
        availableDisplays.removeAll { it.id == displayId }
        availableDisplays.add(
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
            availableDisplays.toList()
        )
    }
}

/**
 * Removes a display from the collection of available displays.
 *
 * This function updates the state by filtering out the specified display,
 * ensuring it is no longer advertised or available for pairing within the current scope.
 *
 * @param service The [NsdServiceInfo] representing the display to be removed.
 * @param availableDisplays The mutable list of currently available displays.
 */
internal fun ProducerScope<List<Display>>.removeAvailableDisplay(
    service: NsdServiceInfo,
    availableDisplays: MutableList<Display>
) {
    val lock = Any()

    synchronized(lock) {
        availableDisplays.removeAll { it.deviceName == service.serviceName }
        trySend(availableDisplays.toList())
    }
}

/**
 * Resolves a discovered network service using the legacy [NsdManager.resolveService] API.
 *
 * This method is used for Android versions prior to Tiramisu (API 33) or where the newer
 * service info callbacks are not supported. Upon successful resolution, it extracts the
 * host address and updates the [availableDisplays] list via [updateAvailableDisplays].
 *
 * @param nsdManager The system service used to resolve the network service.
 * @param service The service discovered on the network that needs to be resolved.
 * @param availableDisplays The mutable list of currently discovered customer displays to be updated.
 */
internal fun ProducerScope<List<Display>>.resolveLegacy(
    nsdManager: NsdManager,
    service: NsdServiceInfo,
    availableDisplays: MutableList<Display>
) {
    @Suppress("DEPRECATION")
    nsdManager.resolveService(service, object : NsdManager.ResolveListener {
        override fun onServiceResolved(resolved: NsdServiceInfo) {
            updateAvailableDisplays(
                service = resolved,
                availableDisplays = availableDisplays
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
 * is updated with network details (like host addresses), it updates the [availableDisplays]
 * list and emits the new state to the [ProducerScope].
 *
 * @param nsdManager The system service used to manage network service discovery.
 * @param service The discovered service that needs to be resolved.
 * @param availableDisplays The mutable list of currently known customer displays to be updated.
 */
internal fun ProducerScope<List<Display>>.resolve(
    nsdManager: NsdManager,
    service: NsdServiceInfo,
    availableDisplays: MutableList<Display>
) {
    if (SdkExtensions.getExtensionVersion(Build.VERSION_CODES.TIRAMISU) >= 7) {
        nsdManager.registerServiceInfoCallback(service, Runnable::run, object : NsdManager.ServiceInfoCallback {
            override fun onServiceUpdated(updated: NsdServiceInfo) {
                updateAvailableDisplays(
                    service = updated,
                    availableDisplays = availableDisplays
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
        resolveLegacy(nsdManager, service, availableDisplays)
    }
}
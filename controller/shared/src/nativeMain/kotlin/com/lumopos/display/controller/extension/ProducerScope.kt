package com.lumopos.display.controller.extension

import com.lumopos.display.data.model.Screen
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.coroutines.channels.ProducerScope
import platform.Network.nw_browse_result_copy_endpoint
import platform.Network.nw_browse_result_t
import platform.Network.nw_endpoint_get_bonjour_service_name
import kotlin.uuid.Uuid

/**
 * Resolves found service by [nw_browse_result_t] and add to [discoveredScreens].
 *
 * @receiver [nw_browse_result_t]
 * @param newResult
 * @param discoveredScreens
 * @param serviceType
 */
@OptIn(ExperimentalForeignApi::class)
internal fun ProducerScope<List<Screen>>.resolveFoundService(
    newResult: nw_browse_result_t,
    discoveredScreens: MutableList<Screen>,
    serviceType: String
) {
    val endpoint = nw_browse_result_copy_endpoint(newResult)
    val serviceName = nw_endpoint_get_bonjour_service_name(endpoint)?.toKString() ?: "unknown"
    val screenId = newResult.id ?: Uuid.random()
    discoveredScreens.removeAll { it.serviceName == serviceName || it.id == screenId }
    discoveredScreens.add(
        Screen(
            id = screenId,
            serviceName = serviceName,
            appName = newResult.appName,
            appAuthor = newResult.appAuthor,
            version = newResult.version,
            serviceType = serviceType
        )
    )
    trySend(discoveredScreens.toList())
}

/**
 * Resolves lost service by [nw_browse_result_t] and remove from [discoveredScreens].
 *
 * @receiver [nw_browse_result_t]
 * @param oldResult
 * @param discoveredScreens
 * @see [nw_endpoint_get_bonjour_service_name]
 * @see [nw_browse_result_copy_endpoint]
 */
@OptIn(ExperimentalForeignApi::class)
internal fun ProducerScope<List<Screen>>.resolveLostService(
    oldResult: nw_browse_result_t,
    discoveredScreens: MutableList<Screen>
) {
    val serviceName =
        nw_endpoint_get_bonjour_service_name(nw_browse_result_copy_endpoint(oldResult))?.toKString()
    serviceName?.let { name ->
        discoveredScreens.removeAll { it.serviceName == name }
        trySend(discoveredScreens.toList())
    }
}

internal fun ProducerScope<List<Screen>>.clearDiscoveredScreens(
    discoveredScreens: MutableList<Screen>
) {
    discoveredScreens.clear()
    trySend(
        discoveredScreens.toList()
    )
}
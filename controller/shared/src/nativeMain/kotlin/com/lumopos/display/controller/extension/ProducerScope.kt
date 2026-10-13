package com.lumopos.display.controller.extension

import com.lumopos.display.data.model.Display
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.coroutines.channels.ProducerScope
import platform.Network.nw_browse_result_copy_endpoint
import platform.Network.nw_browse_result_t
import platform.Network.nw_endpoint_get_bonjour_service_name
import platform.Network.nw_txt_record_access_key
import platform.Network.nw_txt_record_find_key_non_empty_value

/**
 * Resolves found service by [nw_browse_result_t] and add to [discoveredScreens].
 *
 * @receiver [nw_browse_result_t]
 * @param newResult
 * @param discoveredScreens
 * @param serviceType
 */
@OptIn(ExperimentalForeignApi::class)
fun ProducerScope<List<Display>>.resolveFoundService(
    newResult: nw_browse_result_t,
    discoveredScreens: MutableList<Display>,
    serviceType: String
) {
    val endpoint = nw_browse_result_copy_endpoint(newResult)
    val serviceName = nw_endpoint_get_bonjour_service_name(endpoint)?.toKString() ?: "unknown"
    discoveredScreens.add(
        Display(
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
fun ProducerScope<List<Display>>.resolveLostService(
    oldResult: nw_browse_result_t,
    discoveredScreens: MutableList<Display>
) {
    val serviceName =
        nw_endpoint_get_bonjour_service_name(nw_browse_result_copy_endpoint(oldResult))?.toKString()
    serviceName?.let { name ->
        discoveredScreens.removeAll { it.serviceName == name }
        trySend(discoveredScreens.toList())
    }
}

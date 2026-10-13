package com.lumopos.display.discovery.extension

import com.lumopos.display.data.model.Screen
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.coroutines.channels.ProducerScope
import platform.Network.nw_browse_result_copy_endpoint
import platform.Network.nw_browse_result_copy_txt_record_object
import platform.Network.nw_browse_result_t
import platform.Network.nw_endpoint_get_bonjour_service_name
import platform.Network.nw_txt_record_access_key
import platform.Network.nw_txt_record_find_key_non_empty_value

@OptIn(ExperimentalForeignApi::class)
fun nw_browse_result_t.readTxtValue(key: String): String? {
    val txtRecord = nw_browse_result_copy_txt_record_object(this) ?: return null
    var value: String? = null

    memScoped {
        nw_txt_record_access_key(txtRecord, key) { _, found, bytes, length ->
            if (found == nw_txt_record_find_key_non_empty_value && bytes != null) {
                value = bytes.reinterpret<ByteVar>().readBytes(length.toInt()).decodeToString()
            }
            true
        }
    }
    return value
}

@OptIn(ExperimentalForeignApi::class)
fun ProducerScope<List<Screen>>.foundResolve(
    newResult: nw_browse_result_t,
    screens: MutableList<Screen>,
    serviceType: String
) {
    val endpoint = nw_browse_result_copy_endpoint(newResult)
    val serviceName = nw_endpoint_get_bonjour_service_name(endpoint)?.toKString() ?: "unknown"
    screens.add(
        Screen(
            serviceName = serviceName,
            appName = newResult.appName,
            appAuthor = newResult.appAuthor,
            version = newResult.version,
            serviceType = serviceType
        )
    )
    trySend(screens.toList())
}

@OptIn(ExperimentalForeignApi::class)
fun ProducerScope<List<Screen>>.lostResolve(
    oldResult: nw_browse_result_t,
    screens: MutableList<Screen>
) {
    val serviceName =
        nw_endpoint_get_bonjour_service_name(nw_browse_result_copy_endpoint(oldResult))?.toKString()
    serviceName?.let { name ->
        screens.removeAll { it.serviceName == name }
        trySend(screens.toList())
    }
}

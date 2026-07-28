package com.lumopos.display.discovery

import com.lumopos.display.data.model.Display
import com.lumopos.display.discovery.extension.netServiceBrowserDelegate
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.Foundation.NSNetServiceBrowser
import platform.Foundation.NSString
import platform.Foundation.stringWithUTF8String
import platform.Network.nw_browse_descriptor_create_bonjour_service
import platform.Network.nw_browse_result_copy_endpoint
import platform.Network.nw_browse_result_copy_txt_record_object
import platform.Network.nw_browse_result_t
import platform.Network.nw_browser_cancel
import platform.Network.nw_browser_create
import platform.Network.nw_browser_set_browse_results_changed_handler
import platform.Network.nw_browser_set_queue
import platform.Network.nw_browser_set_state_changed_handler
import platform.Network.nw_browser_start
import platform.Network.nw_browser_state_cancelled
import platform.Network.nw_browser_state_failed
import platform.Network.nw_connection_cancel
import platform.Network.nw_connection_copy_current_path
import platform.Network.nw_connection_create
import platform.Network.nw_connection_set_queue
import platform.Network.nw_connection_set_state_changed_handler
import platform.Network.nw_connection_start
import platform.Network.nw_connection_state_cancelled
import platform.Network.nw_connection_state_failed
import platform.Network.nw_connection_state_preparing
import platform.Network.nw_connection_state_ready
import platform.Network.nw_connection_t
import platform.Network.nw_endpoint_copy_address_string
import platform.Network.nw_endpoint_get_bonjour_service_domain
import platform.Network.nw_endpoint_get_bonjour_service_name
import platform.Network.nw_endpoint_get_port
import platform.Network.nw_endpoint_get_type
import platform.Network.nw_endpoint_type_bonjour_service
import platform.Network.nw_parameters_create
import platform.Network.nw_path_copy_effective_local_endpoint
import platform.Network.nw_txt_record_find_key
import platform.Network.nw_txt_record_t
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_create
import kotlin.uuid.Uuid

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DisplayDiscovery() {
    private val availableDisplays: MutableList<Display> = mutableListOf()


    @OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
    actual fun discover(serviceType: String): Flow<List<Display>> = callbackFlow {
        // All Network.framework callbacks for this browse session run on this queue.
        val queue = dispatch_queue_create("discovery.browse.$serviceType", null)

        // key = bonjour service name (unique per advertised instance) -> Display
        val discovered = linkedMapOf<String, Display>()
        // resolver connections currently in flight, so we can cancel them on teardown
        val resolvers = mutableMapOf<String, nw_connection_t>()

        fun emit() = trySend(discovered.values.toList())

        fun serviceKey(result: nw_browse_result_t): String? {
            val endpoint = nw_browse_result_copy_endpoint(result) ?: return null
            val name = nw_endpoint_get_bonjour_service_name(endpoint)?.toKString()
            val domain = nw_endpoint_get_bonjour_service_domain(endpoint)?.toKString() ?: "local"
            return name?.let { "$it.$domain" }
        }

        fun resolveAndStore(key: String, result: nw_browse_result_t) {
            val endpoint = nw_browse_result_copy_endpoint(result) ?: return

            // Resolve the Bonjour endpoint to an actual host/port by briefly connecting to it.
            val params = nw_parameters_create()
            val connection = nw_connection_create(endpoint, params) ?: return
            resolvers[key] = connection

            nw_connection_set_queue(connection, queue)
            nw_connection_set_state_changed_handler(connection) { state, _ ->
                when (state) {
                    nw_connection_state_ready, nw_connection_state_preparing -> {
                        val deviceName = serviceKey(result)
                        val path = nw_connection_copy_current_path(connection)
//                        val remote = path?.let { nw_path_copy_effective_local_endpoint(it) }
//                        val host = remote?.let { nw_endpoint_copy_address_string(it) }?.toKString()
//                        val port = remote?.let { nw_endpoint_get_port(it) }?.toInt()

                        deviceName?.let { deviceName ->
                            discovered[key] = Display(
                                deviceName = deviceName,
                                appName = "appName",
                                appAuthor = "appAuthor",
                                version = "version",
                                serviceType = serviceType,
                                ipAddress = "host",
                                port = 1234
                            )
                        }

                        emit()
                        nw_connection_cancel(connection)
                    }
                    nw_connection_state_failed, nw_connection_state_cancelled -> {
                        resolvers.remove(key)
                    }
                    else -> Unit
                }
            }
            nw_connection_start(connection)
        }

        val descriptor = nw_browse_descriptor_create_bonjour_service(serviceType, null)
        val browseParams = nw_parameters_create()
        val browser = nw_browser_create(descriptor, browseParams)

        nw_browser_set_queue(browser, queue)

        nw_browser_set_state_changed_handler(browser) { state, error ->
            when (state) {
                nw_browser_state_failed -> close(RuntimeException("Bonjour browse failed: $error"))
                nw_browser_state_cancelled -> close()
                else -> Unit
            }
        }

        nw_browser_set_browse_results_changed_handler(browser) { oldResult, newResult, _ ->
            when {
                // Lost: had an old result, nothing new for it.
                newResult == null && oldResult != null -> {
                    serviceKey(oldResult)?.let { key ->
                        discovered.remove(key)
                        resolvers.remove(key)?.let { nw_connection_cancel(it) }
                        emit()
                    }
                }
                // Found / changed
                newResult != null -> {
                    serviceKey(newResult)?.let { key -> resolveAndStore(key, newResult) }
                }
            }
        }

        nw_browser_start(browser)

        awaitClose {
            nw_browser_cancel(browser)
            resolvers.values.forEach { nw_connection_cancel(it) }
        }
    }

    actual fun pauseDiscovering(serviceType: String) {
    }

    @OptIn(BetaInteropApi::class)
    actual fun restartDiscovering(serviceType: String) {
    }

    actual fun stopDiscovering(serviceType: String) {

    }
}
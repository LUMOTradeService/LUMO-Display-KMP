package com.lumopos.display.controller

import com.lumopos.display.controller.extension.resolveFoundService
import com.lumopos.display.controller.extension.resolveLostService
import com.lumopos.display.data.model.Display
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import platform.Network.nw_browse_descriptor_create_bonjour_service
import platform.Network.nw_browse_descriptor_set_include_txt_record
import platform.Network.nw_browser_cancel
import platform.Network.nw_browser_create
import platform.Network.nw_browser_set_browse_results_changed_handler
import platform.Network.nw_browser_set_queue
import platform.Network.nw_browser_set_state_changed_handler
import platform.Network.nw_browser_start
import platform.Network.nw_browser_state_cancelled
import platform.Network.nw_browser_state_failed
import platform.Network.nw_browser_t
import platform.Network.nw_parameters_create
import platform.darwin.dispatch_queue_create

@OptIn(ExperimentalForeignApi::class)
@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class ScreenDiscovery {
    private val discoveredScreens: MutableMap<String, MutableList<Display>> = mutableMapOf()
    private val activeBrowsers: MutableMap<String, nw_browser_t> = mutableMapOf()
    private val restartActions: MutableMap<String, () -> Unit> = mutableMapOf()

    actual fun discover(serviceType: String): kotlinx.coroutines.flow.Flow<List<Display>> = callbackFlow {
        val screens = discoveredScreens.getOrPut(serviceType) { mutableListOf() }
        // All Network.framework callbacks for this browse session run on this queue.
        val queue = dispatch_queue_create("com.lumopos.display.discovery.$serviceType", null)

        fun browsing() {
            val descriptor = memScoped {
                nw_browse_descriptor_create_bonjour_service(serviceType, null)
            }
            nw_browse_descriptor_set_include_txt_record(descriptor, true)
            val parameters = nw_parameters_create()
            val browser = nw_browser_create(descriptor, parameters)

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
                    newResult != null -> {
                        resolveFoundService(newResult, screens, serviceType)
                    }

                    oldResult != null -> {
                        resolveLostService(oldResult, screens)
                    }
                }
            }

            nw_browser_start(browser)
            activeBrowsers.getOrPut(serviceType) { browser }
        }

        restartActions[serviceType] = ::browsing
        browsing()

        awaitClose {
            stopDiscovering(serviceType)
        }
    }.flowOn(Dispatchers.IO)

    actual fun pauseDiscovering(serviceType: String) {
        activeBrowsers.remove(serviceType)?.let { nw_browser_cancel(it) }
    }

    actual fun restartDiscovering(serviceType: String) {
        if (activeBrowsers.containsKey(serviceType)) return
        restartActions[serviceType]?.invoke()
    }

    actual fun stopDiscovering(serviceType: String) {
        activeBrowsers.remove(serviceType)?.let { nw_browser_cancel(it) }
        restartActions.remove(serviceType)
        discoveredScreens.remove(serviceType)
    }
}
package com.lumopos.display.controller

import com.lumopos.display.controller.extension.appAuthor
import com.lumopos.display.controller.extension.appName
import com.lumopos.display.controller.extension.clearDiscoveredScreens
import com.lumopos.display.controller.extension.id
import com.lumopos.display.controller.extension.resolveFoundService
import com.lumopos.display.controller.extension.resolveLostService
import com.lumopos.display.controller.extension.version
import com.lumopos.display.data.model.Screen
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import platform.Network.nw_browse_descriptor_create_bonjour_service
import platform.Network.nw_browse_descriptor_set_include_txt_record
import platform.Network.nw_browse_result_copy_endpoint
import platform.Network.nw_browser_cancel
import platform.Network.nw_browser_create
import platform.Network.nw_browser_set_browse_results_changed_handler
import platform.Network.nw_browser_set_queue
import platform.Network.nw_browser_set_state_changed_handler
import platform.Network.nw_browser_start
import platform.Network.nw_browser_state_cancelled
import platform.Network.nw_browser_state_failed
import platform.Network.nw_browser_state_ready
import platform.Network.nw_browser_t
import platform.Network.nw_endpoint_get_bonjour_service_name
import platform.Network.nw_parameters_create
import platform.darwin.dispatch_queue_create
import kotlin.uuid.Uuid

@OptIn(ExperimentalForeignApi::class)
@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class ScreenDiscovery {
    private val discoveredScreens: MutableMap<String, MutableList<Screen>> = mutableMapOf()
    private val browsers: MutableMap<String, nw_browser_t> = mutableMapOf()
    private var screenDiscoveryListeners: MutableList<Listener> = mutableListOf()

    private fun createBrowser(serviceType: String): nw_browser_t {
        val queue =
            dispatch_queue_create("com.lumopos.display.controller.discovery.$serviceType", null)
        val descriptor = memScoped {
            nw_browse_descriptor_create_bonjour_service(serviceType, null)
        }
        nw_browse_descriptor_set_include_txt_record(descriptor, true)
        val parameters = nw_parameters_create()
        val browser = nw_browser_create(descriptor, parameters)

        nw_browser_set_queue(browser, queue)
        return browser
    }

    actual fun screens(serviceType: String): Flow<List<Screen>> = callbackFlow {
        val listener = object : Listener {

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

            override fun onDiscoveryFailed(errorCode: String) {

            }

        }

        screenDiscoveryListeners.add(listener)

        awaitClose {
            screenDiscoveryListeners.remove(listener)
        }
    }.flowOn(Dispatchers.IO)

    actual fun startDiscovering(serviceType: String) {
        val browser = createBrowser(serviceType)

        nw_browser_set_browse_results_changed_handler(browser) { oldResult, newResult, _ ->
            when {
                newResult != null -> {
                    val screens = discoveredScreens.getOrPut(serviceType) { mutableListOf() }
                    val endpoint = nw_browse_result_copy_endpoint(newResult)
                    val serviceName = nw_endpoint_get_bonjour_service_name(endpoint)?.toKString() ?: "unknown"
                    val screenId = newResult.id ?: Uuid.random()
                    screens.removeAll { it.serviceName == serviceName || it.id == screenId }
                    screens.add(
                        Screen(
                            id = screenId,
                            serviceName = serviceName,
                            appName = newResult.appName,
                            appAuthor = newResult.appAuthor,
                            version = newResult.version,
                            serviceType = serviceType
                        )
                    )
                }

                oldResult != null -> {
                    discoveredScreens[serviceType]?.let { screens ->
                        val serviceName =
                            nw_endpoint_get_bonjour_service_name(nw_browse_result_copy_endpoint(oldResult))?.toKString()
                        serviceName?.let { name ->
                            screens.removeAll { it.serviceName == name }
                        }
                    }
                }
            }
        }

        nw_browser_set_state_changed_handler(browser) { state, error ->
            when (state) {
                nw_browser_state_ready -> {
                    discoveredScreens[serviceType]?.let {
                        discoveredScreens.clear()
                    }
                }

                nw_browser_state_cancelled -> {}
                nw_browser_state_failed -> {
                    screenDiscoveryListeners.forEach { listener ->
                        listener.onDiscoveryFailed(error.toString())
                    }
                    throw RuntimeException("Bonjour browse failed: $error")
                }
                else -> Unit
            }
        }

        browsers.getOrPut(serviceType) { browser }

        browsers[serviceType]?.let {
            nw_browser_start(it)
        }
    }

    actual fun pauseDiscovering(serviceType: String) {
        browsers[serviceType]?.let { nw_browser_cancel(it) }
    }

    actual fun stopDiscovering(serviceType: String) {
        browsers.remove(serviceType)?.let { nw_browser_cancel(it) }
    }

    actual interface Listener {
        actual fun onServiceFound(screen: Screen)
        actual fun onServiceLost(screen: Screen)
        actual fun onDiscoveryStarted()
        actual fun onDiscoveryStopped()
        actual fun onDiscoveryFailed(errorCode: String)
    }
}
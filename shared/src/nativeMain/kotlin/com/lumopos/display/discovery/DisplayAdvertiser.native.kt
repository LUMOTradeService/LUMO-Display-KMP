package com.lumopos.display.discovery

import com.lumopos.display.data.model.Display
import com.lumopos.display.data.model.currentAppVersion
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.cstr
import kotlinx.cinterop.objcPtr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.Network.NW_PARAMETERS_DEFAULT_CONFIGURATION
import platform.Network.NW_PARAMETERS_DISABLE_PROTOCOL
import platform.Network.nw_advertise_descriptor_create_bonjour_service
import platform.Network.nw_advertise_descriptor_set_txt_record_object
import platform.Network.nw_listener_cancel
import platform.Network.nw_listener_create_with_port
import platform.Network.nw_listener_set_advertise_descriptor
import platform.Network.nw_listener_set_new_connection_handler
import platform.Network.nw_listener_set_queue
import platform.Network.nw_listener_set_state_changed_handler
import platform.Network.nw_listener_start
import platform.Network.nw_listener_state_failed
import platform.Network.nw_listener_state_ready
import platform.Network.nw_listener_t
import platform.Network.nw_parameters_create_secure_tcp
import platform.Network.nw_parameters_set_reuse_local_address
import platform.Network.nw_txt_record_create_dictionary
import platform.Network.nw_txt_record_set_key
import platform.darwin.dispatch_get_main_queue

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class DisplayAdvertiser(
    appName: String,
    appAuthor: String,
    serviceType: String,
) {
    private var listener: nw_listener_t = null

    actual var display: Display = Display(
        appName = appName,
        appAuthor = appAuthor,
        version = currentAppVersion(),
        serviceType = serviceType
    )

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun advertise() {
        val serviceName = display.serviceName
        val serviceType = display.serviceType
        val parameters = nw_parameters_create_secure_tcp(
            NW_PARAMETERS_DISABLE_PROTOCOL,
            /* TCP = */ NW_PARAMETERS_DEFAULT_CONFIGURATION
        )
        nw_parameters_set_reuse_local_address(parameters, true)
        listener = nw_listener_create_with_port(display.port.toString(), parameters)
        val descriptor = nw_advertise_descriptor_create_bonjour_service(serviceName, serviceType, null)
        val txtRecord = nw_txt_record_create_dictionary()
        mapOf(
            DisplayAdvertiserConstants.APP_VERSION to display.version,
            DisplayAdvertiserConstants.APP_NAME to display.appName,
            DisplayAdvertiserConstants.APP_AUTHOR to display.appAuthor,
        ).forEach { (key, value) ->
            val bytes = value.encodeToByteArray()
            if (bytes.isNotEmpty()) {
                bytes.usePinned {
                    nw_txt_record_set_key(txtRecord, key, it.addressOf(0).reinterpret(), bytes.size.convert())
                }
            }
        }

        nw_advertise_descriptor_set_txt_record_object(descriptor, txtRecord)
        nw_listener_set_advertise_descriptor(listener, descriptor)
        nw_listener_set_queue(listener, dispatch_get_main_queue())
        nw_listener_set_new_connection_handler(listener) { connection ->
            if(connection != null) {
                println("Bonjour: New connection")
            }
        }
        nw_listener_set_state_changed_handler(listener) { state, error ->
            when (state) {
                nw_listener_state_ready -> println("Bonjour: Listener ready name: ${display.serviceName}, port: ${display.port}, type: ${display.serviceType}")
                nw_listener_state_failed -> println("Bonjour: Listener failed with error: $error")
                else -> {}
            }
        }
        nw_listener_start(listener)
    }

    actual fun stopAdvertising() {
        listener?.let {
            nw_listener_cancel(it)
            listener = null
        }
    }
}
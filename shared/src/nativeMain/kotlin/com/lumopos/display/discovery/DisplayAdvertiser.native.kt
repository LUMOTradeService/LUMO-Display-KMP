package com.lumopos.display.discovery

import com.lumopos.display.data.model.Display
import com.lumopos.display.data.model.currentAppVersion
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Network.nw_advertise_descriptor_create_bonjour_service
import platform.Network.nw_listener_cancel
import platform.Network.nw_listener_create_with_port
import platform.Network.nw_listener_set_advertise_descriptor
import platform.Network.nw_listener_set_state_changed_handler
import platform.Network.nw_listener_start
import platform.Network.nw_listener_state_failed
import platform.Network.nw_listener_state_ready
import platform.Network.nw_listener_t
import platform.Network.nw_parameters_create_secure_tcp
import platform.Network.nw_parameters_set_reuse_local_address

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
        val serviceName = display.deviceName
        val serviceType = display.serviceType
        val parameters = nw_parameters_create_secure_tcp(
            /* TLS = */ null,
            /* TCP = */ null
        )
        nw_parameters_set_reuse_local_address(parameters, true)
        listener = nw_listener_create_with_port(display.port.toString(), parameters)
        val descriptor = nw_advertise_descriptor_create_bonjour_service(serviceName, serviceType, null)

//        val txtRecord = nw_txt_record_create_dictionary()
//        nw_txt_record_set_key(txtRecord, "appName", display.appName)
//        nw_advertise_descriptor_set_txt_record_object(descriptor, txtRecord)

        nw_listener_set_advertise_descriptor(listener, descriptor)
        nw_listener_set_state_changed_handler(listener) { state, error ->
            when (state) {
                nw_listener_state_ready -> println("Bonjour: Služba úspěšně inzerována na portu ${display.port}")
                nw_listener_state_failed -> println("Bonjour: Selhalo spuštění listeneru: $error")
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
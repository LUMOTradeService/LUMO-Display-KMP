package com.lumopos.display.discovery

import com.lumopos.display.data.Display

object DisplayAdvertiserConstants {
    const val APP_NAME = "app_name"
    const val APP_AUTHOR = "app_author"
    const val APP_VERSION = "version"
    const val SERVICE_TYPE = "service_type"
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DisplayAdvertiser {
    /**
     * The local device information that is being advertised to other nodes on the network.
     */
    var display: Display
        private set

    /**
     * Starts broadcasting the service on the local network using the defined service type.
     * This makes the current device discoverable to other instances by publishing
     * its device information and metadata.
     */
    suspend fun advertise()
    /**
     * Stops the ongoing service advertisement, making the device no longer
     * discoverable by other nodes on the network.
     */
    fun stopAdvertising()
}
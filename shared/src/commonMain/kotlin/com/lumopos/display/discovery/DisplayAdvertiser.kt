package com.lumopos.display.discovery

import com.lumopos.display.data.model.Display

/**
 * Responsible for advertising the current device's presence and metadata on the local network.
 *
 * This class handles the network service registration process, allowing other nodes
 * to discover this device's [Display] information. It utilizes the configuration
 * defined in [ScreenServiceConstants] to broadcast the service type and application metadata.
 */
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
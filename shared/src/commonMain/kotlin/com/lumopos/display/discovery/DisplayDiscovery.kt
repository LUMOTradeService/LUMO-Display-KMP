package com.lumopos.display.discovery

import com.lumopos.display.data.Display
import kotlinx.coroutines.flow.Flow

/**
 * A platform-specific service discovery manager responsible for advertising the current device
 * and discovering other Customer Display services on the local network.
 *
 * This class facilitates the connection process between devices by using network service
 * discovery protocols (such as mDNS/DNS-SD) to broadcast its own presence and scan
 * for available [Display] nodes.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DisplayDiscovery {
    /**
     * Starts the service discovery process to find available customer displays on the local network.
     *
     * @return A [Flow] that emits an updated list of [Display] objects whenever
     * a display is discovered or lost.
     */
    fun discover(serviceType: String): Flow<List<Display>>
    /**
     * Pauses the active service discovery process.
     *
     * Unlike [stopDiscovering], this typically maintains the discovery state or
     * resources but suspends the emission of updates until resumed or restarted.
     */
    fun pauseDiscovering(serviceType: String)
    /**
     * Restarts the service discovery process by stopping any ongoing discovery
     * and initiating a fresh scan for available customer displays on the local network.
     */
    fun restartDiscovering(serviceType: String)
    /**
     * Stops the active discovery of Customer Display services.
     *
     * This method halts the scanning process initiated by [discover] and releases
     * any resources associated with the discovery lifecycle.
     */
    fun stopDiscovering(serviceType: String)
}
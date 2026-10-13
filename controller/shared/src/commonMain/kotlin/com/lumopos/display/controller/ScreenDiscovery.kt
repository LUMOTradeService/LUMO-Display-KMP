package com.lumopos.display.controller

import com.lumopos.display.data.model.Screen
import kotlinx.coroutines.flow.Flow

/**
 * A platform-specific service discovery manager responsible for advertising the current device
 * and discovering other Customer Display services on the local network.
 *
 * This class facilitates the connection process between devices by using network service
 * discovery protocols (such as mDNS/DNS-SD) to broadcast its own presence and scan
 * for available [Screen] nodes.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class ScreenDiscovery {
    interface ScreenDiscoveryListener {
        fun onServiceFound(screen: Screen)
        fun onServiceLost(screen: Screen)
        fun onDiscoveryStarted()
        fun onDiscoveryStopped()
        fun onStartDiscoveryFailed(errorCode: Int)
        fun onStopDiscoveryFailed(errorCode: Int)
    }

    /**
     * Starts the service discovery process to find available customer displays on the local network.
     *
     * @return A [Flow] that emits an updated list of [Screen] objects whenever
     * a display is discovered or lost.
     */
    fun screens(serviceType: String): Flow<List<Screen>>

    /**
     * Starts the service discovery process to find available customer displays on the local network.
     *
     * @param serviceType The type of service to discover.
     */
    fun startDiscovering(serviceType: String)
    /**
     * Pauses the active service discovery process.
     *
     * Unlike [stopDiscovering], this typically maintains the discovery state or
     * resources but suspends the emission of updates until resumed or restarted.
     */
    fun pauseDiscovering(serviceType: String)
    /**
     * Stops the active discovery of Customer Display services.
     *
     * This method halts the scanning process initiated by [screens] and releases
     * any resources associated with the discovery lifecycle.
     */
    fun stopDiscovering(serviceType: String)
}
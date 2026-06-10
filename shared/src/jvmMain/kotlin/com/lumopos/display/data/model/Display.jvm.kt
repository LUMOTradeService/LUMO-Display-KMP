package com.lumopos.display.data.model

import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import kotlin.collections.iterator

actual fun getDeviceName(): String {
    val hostname = try {
        InetAddress.getLocalHost().hostName
    } catch (e: Exception) {
        "Unknown"
    }
    return hostname
}

internal actual fun getLocalIpAddress(): String {
    var foundAddress: String? = null
    try {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        for (networkInterface in networkInterfaces) {
            val addresses = networkInterface.inetAddresses
            for (address in addresses) {
                if (!address.isLoopbackAddress && address is Inet4Address) {
                    foundAddress = address.hostAddress
                }
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return foundAddress ?: "unknown"
}

internal actual fun getPort(): Int {
    // Initialize a server socket on the next available port.
    ServerSocket(0).use { socket ->
        // Store the chosen port.
        return socket.localPort
    }
}

fun currentAppVersion(): String {
    return System.getProperty("app.version") ?: "1.0.0"
}
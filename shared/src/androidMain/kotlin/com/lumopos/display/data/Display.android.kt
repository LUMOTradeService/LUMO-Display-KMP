package com.lumopos.display.data

import android.content.Context
import android.os.Build
import android.provider.Settings
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.ServerSocket

actual fun getDeviceName(): String {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    return if (model.startsWith(manufacturer)) {
        model.replaceFirstChar { it.uppercase() }
    } else {
        "$manufacturer $model".replaceFirstChar { it.uppercase() }
    }
}

fun getDeviceName(context: Context): String {
    return try {
        Settings.Global.getString(
            context.contentResolver,
            Settings.Global.DEVICE_NAME
        )
    } catch (e: Exception) {
        e.printStackTrace()
        getDeviceName()
    }
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

fun currentAppVersion(context: Context): String {
    return context.packageManager
        .getPackageInfo(
            context.packageName,
            0
        ).versionName ?: "Unknown"
}
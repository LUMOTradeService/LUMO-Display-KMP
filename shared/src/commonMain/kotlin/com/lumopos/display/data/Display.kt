package com.lumopos.display.data

import kotlinx.serialization.Serializable

@Serializable
data class Display(
    val deviceName: String = getDeviceName(),
    val appName: String,
    val appAuthor: String,
    val version: String,
    val serviceType: String,
    var ipAddress: String = getLocalIpAddress(),
    var port: Int = getPort()
)

expect fun getDeviceName(): String

internal expect fun getLocalIpAddress(): String

internal expect fun getPort(): Int
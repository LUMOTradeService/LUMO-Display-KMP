package com.lumopos.display.data

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class Display(
    val id: Uuid = Uuid.random(),
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
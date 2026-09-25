package com.lumopos.display.data.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class Display(
    val id: Uuid = Uuid.random(),
    val appName: String,
    val appAuthor: String,
    val version: String,
    val serviceName: String = getDeviceName(),
    val serviceType: String,
    var ipAddress: String? = getLocalIpAddress(),
    var port: Int? = getPort()
)

expect fun getDeviceName(): String

internal expect fun getLocalIpAddress(): String

internal expect fun getPort(): Int
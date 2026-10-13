package com.lumopos.display.controller.extension

import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.os.ext.SdkExtensions
import com.lumopos.display.discovery.ScreenServiceConstants
import kotlin.uuid.Uuid

/**
 * Extracts the [Uuid] from the [NsdServiceInfo] attributes using the predefined service discovery ID key.
 *
 * @return The parsed [Uuid] if the attribute exists and is valid, or `null` otherwise.
 */
internal val NsdServiceInfo.id: Uuid?
    get() = this.attributes[ScreenServiceConstants.ID]?.let {
        Uuid.parse(it.decodeToString())
    }

/**
 * Extracts the application version from the [NsdServiceInfo] attributes.
 *
 * @return The version string if found and decodable, otherwise returns "unknown".
 */
internal val NsdServiceInfo.appVersion: String
    get() = this.attributes[ScreenServiceConstants.APP_VERSION]?.decodeToString() ?: "unknown"

/**
 * Retrieves the IP address from the [NsdServiceInfo].
 *
 * This function handles compatibility across different Android versions, using `hostAddresses`
 * for Tiramisu (API 33) extension version 7 and above, and falling back to the deprecated
 * `host` property for older versions.
 *
 * @return The string representation of the host's IP address, or "unknown" if not found.
 */
internal val NsdServiceInfo.ipAddress: String
    get() = if (SdkExtensions.getExtensionVersion(Build.VERSION_CODES.TIRAMISU) >= 7) {
        hostAddresses.firstOrNull()?.hostAddress ?: "unknown"
    } else {
        @Suppress("DEPRECATION")
        host?.hostAddress ?: "unknown"
    }

internal val NsdServiceInfo.appName: String
    get() = this.attributes[ScreenServiceConstants.APP_NAME]?.decodeToString()
        ?: "unknown"

internal val NsdServiceInfo.appAuthor: String
    get() = this.attributes[ScreenServiceConstants.APP_AUTHOR]?.decodeToString()
        ?: "unknown"
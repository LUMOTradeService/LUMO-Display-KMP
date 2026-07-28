package com.lumopos.display.discovery.extension

import com.lumopos.display.data.model.Display
import com.lumopos.display.discovery.DisplayAdvertiserConstants
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import kotlinx.cinterop.pointed
import kotlinx.cinterop.readValue
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.coroutines.channels.ProducerScope
import platform.Foundation.NSData
import platform.Foundation.NSNetService
import platform.Foundation.NSNetServiceBrowser
import platform.Foundation.NSNetServiceBrowserDelegateProtocol
import platform.Foundation.NSNetServiceDelegateProtocol
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.darwin.NSObject
import platform.darwin.inet_ntoa
import platform.posix.AF_INET
import platform.posix.sockaddr
import platform.posix.sockaddr_in
import kotlin.uuid.Uuid

internal fun ProducerScope<List<Display>>.netServiceBrowserDelegate(
    discoveredDisplays: MutableList<Display>,
): NSNetServiceBrowserDelegateProtocol {
    return object : NSObject(), NSNetServiceBrowserDelegateProtocol {
        @ObjCSignatureOverride
        override fun netServiceBrowser(
            browser: NSNetServiceBrowser,
            didFindService: NSNetService,
            moreComing: Boolean
        ) {
            resolve(didFindService, discoveredDisplays)
        }

        @ObjCSignatureOverride
        override fun netServiceBrowser(
            browser: NSNetServiceBrowser,
            didRemoveService: NSNetService,
            moreComing: Boolean
        ) {
            resolve(didRemoveService, discoveredDisplays, isFound = false)
        }
    }
}

/**
 * Resolves a network service using the provided [NSNetService] and emits the result
 * or any potential errors through the [ProducerScope].
 *
 * This extension function handles the asynchronous process of resolving a service's
 * network address and metadata, ensuring that the results are communicated back
 * to the flow collector.
 *
 * @param service The [NSNetService] instance representing the service to be resolved.
 * @param discoveredDisplays The mutable list of available displays to be updated.
 */
@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
internal fun ProducerScope<List<Display>>.resolve(
    service: NSNetService,
    discoveredDisplays: MutableList<Display>,
    isFound: Boolean = true
) {
    service.resolveWithTimeout(5.0)
    service.delegate = object : NSObject(), NSNetServiceDelegateProtocol {
        override fun netServiceDidResolveAddress(sender: NSNetService) {
            val lock = SynchronizedObject()
            synchronized(lock) {
                val displayId = try {
                    sender.getTxtValue(DisplayAdvertiserConstants.ID)?.let {
                        Uuid.parse(it)
                    } ?: return
                } catch (_: Exception) {
                    return
                }
                discoveredDisplays.removeAll { it.id == displayId }
                if (isFound) {
                    val ipAddress =
                        sender.addresses?.firstOrNull()?.let { it as NSData }?.toIpAddress()
                            ?: "unknown"
                    discoveredDisplays.add(
                        Display(
                            id = displayId,
                            deviceName = sender.name,
                            ipAddress = ipAddress,
                            port = sender.port.toInt(),
                            serviceType = sender.getTxtValue(DisplayAdvertiserConstants.SERVICE_TYPE)
                                ?: "unknown",
                            appName = sender.getTxtValue(DisplayAdvertiserConstants.APP_NAME)
                                ?: "unknown",
                            appAuthor = sender.getTxtValue(DisplayAdvertiserConstants.APP_AUTHOR)
                                ?: "unknown",
                            version = sender.getTxtValue(DisplayAdvertiserConstants.APP_VERSION)
                                ?: "unknown"
                        )
                    )
                }
                trySend(
                    discoveredDisplays.toList()
                )
            }
        }
    }
}

@OptIn(BetaInteropApi::class)
private fun NSNetService.getTxtValue(key: String): String? {
    val txtData = TXTRecordData() ?: return null

    val dict = NSNetService.dictionaryFromTXTRecordData(txtData)

    val data = dict[key] as? NSData ?: return null
    return NSString.create(data, NSUTF8StringEncoding)?.toString()
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toIpAddress(): String? {
    val bytes = bytes ?: return null
    val sockaddr = bytes.reinterpret<sockaddr>()
    return if (sockaddr.pointed.sa_family.toInt() == AF_INET) {
        val sockaddrIn = bytes.reinterpret<sockaddr_in>()
        inet_ntoa(sockaddrIn.pointed.sin_addr.readValue())?.toKString()
    } else {
        null
    }
}

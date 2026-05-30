package com.lumopos.display.discovery.extension

import com.lumopos.display.data.model.Display
import com.lumopos.display.discovery.DisplayAdvertiserConstants
import com.lumopos.display.pair.DisplayPairingConstants
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCSignatureOverride
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
import kotlin.uuid.Uuid

internal fun ProducerScope<List<Display>>.netServiceBrowserDelegate(
    availableDisplays: MutableList<Display>,
): NSNetServiceBrowserDelegateProtocol {
    return object : NSObject(), NSNetServiceBrowserDelegateProtocol {
        @ObjCSignatureOverride
        override fun netServiceBrowser(
            browser: NSNetServiceBrowser,
            didFindService: NSNetService,
            moreComing: Boolean
        ) {
            netServiceBrowserResolve(didFindService, availableDisplays)
        }

        @ObjCSignatureOverride
        override fun netServiceBrowser(
            browser: NSNetServiceBrowser,
            didRemoveService: NSNetService,
            moreComing: Boolean
        ) {
            netServiceBrowserResolve(didRemoveService, availableDisplays, false)
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
 * @param availableDisplays The mutable list of available displays to be updated.
 * @param update A flag indicating whether to update the list of available displays.
 */
@OptIn(BetaInteropApi::class)
internal fun ProducerScope<List<Display>>.netServiceBrowserResolve(
    service: NSNetService,
    availableDisplays: MutableList<Display>,
    update: Boolean = true
) {
    service.resolveWithTimeout(5.0)
    service.delegate = object : NSObject(), NSNetServiceDelegateProtocol {
        override fun netServiceDidResolveAddress(sender: NSNetService) {
            val txtData = sender.TXTRecordData()
            val dict =
                if (txtData != null) NSNetService.dictionaryFromTXTRecordData(txtData) else null

            fun getTxtValue(key: String): String? {
                val data = dict?.getValue(key) as NSData
                return NSString.create(data, NSUTF8StringEncoding)?.toString()
            }

            val id = Uuid.parse(getTxtValue(DisplayAdvertiserConstants.ID) ?: return)

            availableDisplays.removeAll { it.id == id }
            if (update) {
                availableDisplays.add(
                    Display(
                        id = id,
                        name = sender.name,
                        ipAddress = sender.addresses?.first().toString(),
                        port = sender.port.toInt(),
                        pairingPath = getTxtValue(DisplayAdvertiserConstants.PAIRING_PATH)
                            ?: DisplayPairingConstants.PAIRING_PATH_DEFAULT,
                        sessionPath = getTxtValue(DisplayAdvertiserConstants.SESSION_PATH)
                            ?: Display.SESSION_PATH_DEFAULT,
                        serviceType = getTxtValue(DisplayAdvertiserConstants.SERVICE_TYPE)
                            ?: "unknown",
                        appName = getTxtValue(DisplayAdvertiserConstants.APP_NAME)
                            ?: "unknown",
                        appAuthor = getTxtValue(DisplayAdvertiserConstants.APP_AUTHOR)
                            ?: "unknown",
                        version = getTxtValue(DisplayAdvertiserConstants.APP_VERSION)
                            ?: "unknown"
                    )
                )
            }

            trySend(availableDisplays.toList())
        }
    }
}
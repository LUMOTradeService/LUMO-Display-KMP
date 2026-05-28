package com.lumopos.display.discovery

import com.lumopos.display.data.Display
import com.lumopos.display.data.currentAppVersion
import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSNetService
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class DisplayAdvertiser(
    appName: String,
    appAuthor: String,
    serviceType: String,
) {
    private var netService: NSNetService? = null

    actual var display: Display = Display(
        appName = appName,
        appAuthor = appAuthor,
        version = currentAppVersion(),
        serviceType = serviceType
    )

    @OptIn(BetaInteropApi::class)
    actual suspend fun advertise() {
        netService = NSNetService(
            domain = "local.",
            type = display.serviceType,
            name = display.deviceName,
            port = display.port
        ).apply {
            val nsData = NSNetService.dataFromTXTRecordDictionary(
                mapOf(
                    DisplayAdvertiserConstants.APP_NAME as Any? to (NSString.create(
                        display.appName
                    ))?.dataUsingEncoding(NSUTF8StringEncoding),
                    DisplayAdvertiserConstants.APP_VERSION as Any? to (NSString.create(
                        display.version
                    ))?.dataUsingEncoding(NSUTF8StringEncoding)
                )
            )
            setTXTRecordData(nsData)
            publish()
        }
    }

    actual fun stopAdvertising() {
        netService?.stop()
        netService = null
    }
}
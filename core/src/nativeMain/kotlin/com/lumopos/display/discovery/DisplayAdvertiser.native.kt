package com.lumopos.display.discovery

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class DisplayAdvertiser {
    private val discoveryScope = CoroutineScope(Dispatchers.IO)
    private var netService: NSNetService? = null

    actual var display: com.lumopos.display.data.Display
        get() = TODO("Not yet implemented")
        set(value) {}

    actual suspend fun advertise() {
        netService = NSNetService(
            domain = "local.",
            type = display.serviceType,
            name = display.name,
            port = display.port
        ).apply {
            val nsData = NSNetService.dataFromTXTRecordDictionary(
                mapOf(
                    DisplayAdvertiserConstants.ID as Any? to (NSString.create(display.id.toString())).dataUsingEncoding(NSUTF8StringEncoding),
                    DisplayAdvertiserConstants.APP_NAME as Any? to (NSString.create(
                        display.appName
                    )).dataUsingEncoding(NSUTF8StringEncoding),
                    DisplayAdvertiserConstants.APP_VERSION as Any? to (NSString.create(
                        display.version
                    )).dataUsingEncoding(NSUTF8StringEncoding),
                    DisplayAdvertiserConstants.PAIRING_PATH as Any? to (NSString.create(
                        display.pairingPath
                    )).dataUsingEncoding(NSUTF8StringEncoding),
                    DisplayAdvertiserConstants.SESSION_PATH as Any? to (NSString.create(
                        display.sessionPath
                    )).dataUsingEncoding(NSUTF8StringEncoding)
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
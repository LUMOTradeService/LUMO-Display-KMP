package com.lumopos.display.discovery

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import com.lumopos.display.data.model.Display
import com.lumopos.display.data.model.currentAppVersion
import com.lumopos.display.data.model.getDeviceName

@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class DisplayAdvertiser(
    private val context: Context,
    appName: String,
    appAuthor: String,
    serviceType: String,
    ) {
    private val nsdManager = context.getSystemService(
        Context.NSD_SERVICE
    ) as NsdManager
    private var registrationListener: NsdManager.RegistrationListener? = null

    actual var display: Display = Display(
        deviceName = getDeviceName(context),
        appName = appName,
        appAuthor = appAuthor,
        version = currentAppVersion(context),
        serviceType = serviceType
    )
        private set

    actual suspend fun advertise() {
        val serviceInfo = NsdServiceInfo().apply {
            this.serviceType = display.serviceType
            this.serviceName = display.deviceName
            this.port = display.port
            setAttribute(DisplayAdvertiserConstants.APP_NAME, display.appName)
            setAttribute(DisplayAdvertiserConstants.APP_AUTHOR, display.appAuthor)
            setAttribute(DisplayAdvertiserConstants.APP_VERSION, display.version)
        }

        registrationListener = object : NsdManager.RegistrationListener {
            override fun onRegistrationFailed(service: NsdServiceInfo?, errorCode: Int) {}
            override fun onServiceRegistered(service: NsdServiceInfo?) {}
            override fun onServiceUnregistered(service: NsdServiceInfo?) {}
            override fun onUnregistrationFailed(service: NsdServiceInfo?, p1: Int) {}
        }

        nsdManager.registerService(
            serviceInfo,
            NsdManager.PROTOCOL_DNS_SD,
            registrationListener
        )
    }

    actual fun stopAdvertising() {
        registrationListener?.let {
            nsdManager.unregisterService(it)
        }
        registrationListener = null
    }
}
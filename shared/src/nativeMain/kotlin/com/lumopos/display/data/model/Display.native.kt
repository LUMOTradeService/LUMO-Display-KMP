package com.lumopos.display.data.model

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readValue
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import platform.CoreFoundation.CFSwapInt16BigToHost
import platform.Foundation.NSBundle
import platform.UIKit.UIDevice
import platform.darwin.freeifaddrs
import platform.darwin.getifaddrs
import platform.darwin.ifaddrs
import platform.darwin.inet_ntoa
import platform.posix.AF_INET
import platform.posix.INADDR_ANY
import platform.posix.SOCK_STREAM
import platform.posix.bind
import platform.posix.close
import platform.posix.getsockname
import platform.posix.sockaddr_in
import platform.posix.socket
import platform.posix.socklen_tVar

actual fun getDeviceName(): String {
    return UIDevice.currentDevice.name
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun getLocalIpAddress(): String {
    var address: String? = null
    memScoped {
        val ifap = allocPointerTo<ifaddrs>()

        if (getifaddrs(ifap.ptr) == 0) {
            var curr: ifaddrs? = ifap.value?.pointed

            while (curr != null) {
                val addr = curr.ifa_addr
                if (addr?.pointed?.sa_family?.toInt() == AF_INET) {
                    val name = curr.ifa_name?.toKString()

                    if (name == "en0") {
                        val sin = addr.reinterpret<sockaddr_in>().pointed
                        address = inet_ntoa(sin.sin_addr.readValue())?.toKString()
                        break
                    }
                }
                curr = curr.ifa_next?.pointed
            }
            freeifaddrs(ifap.value)
        }
    }
    return address ?: "unknown"
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun getPort(): Int {
    memScoped {
        val fd = socket(AF_INET, SOCK_STREAM, 0)
        if (fd < 0) return -1

        val serverAddr = alloc<sockaddr_in>()
        serverAddr.sin_family = AF_INET.convert()
        serverAddr.sin_port = 0.convert()
        serverAddr.sin_addr.s_addr = INADDR_ANY.convert()

        if (bind(fd, serverAddr.ptr.reinterpret(), sizeOf<sockaddr_in>().convert()) == 0) {
            val addrLen = alloc<socklen_tVar>()
            addrLen.value = sizeOf<sockaddr_in>().convert()

            if (getsockname(fd, serverAddr.ptr.reinterpret(), addrLen.ptr) == 0) {
                val port = CFSwapInt16BigToHost(serverAddr.sin_port)
                close(fd)
                return port.toInt()
            }
        }

        close(fd)
        return -1
    }
}

fun currentAppVersion(): String {
    return NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as String
}
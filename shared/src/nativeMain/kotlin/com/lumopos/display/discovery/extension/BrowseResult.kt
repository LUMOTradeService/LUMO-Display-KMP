package com.lumopos.display.discovery.extension

import platform.Network.nw_browse_result_t

val nw_browse_result_t.version get() = this.readTxtValue("version") ?: "unknown"
val nw_browse_result_t.appName get() = this.readTxtValue("app_name") ?: "unknown"
val nw_browse_result_t.appAuthor get() = this.readTxtValue("app_author") ?: "unknown"
package com.lumopos.display.controller.extension

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import platform.Network.nw_browse_result_copy_txt_record_object
import platform.Network.nw_browse_result_t
import platform.Network.nw_txt_record_access_key
import platform.Network.nw_txt_record_find_key_non_empty_value

/**
 * Get Value from TXT Record by Key.
 *
 * @receiver [nw_browse_result_t]
 * @param key
 * @return [String]
 * @see [nw_txt_record_access_key]
 * @see [nw_txt_record_find_key_non_empty_value]
 * @see [nw_browse_result_copy_txt_record_object]
 * @see [memScoped]
 * @see [readBytes]
 */
@OptIn(ExperimentalForeignApi::class)
fun nw_browse_result_t.readTxtValue(key: String): String? {
    val txtRecord = nw_browse_result_copy_txt_record_object(this) ?: return null
    var value: String? = null

    memScoped {
        nw_txt_record_access_key(txtRecord, key) { _, found, bytes, length ->
            if (found == nw_txt_record_find_key_non_empty_value && bytes != null) {
                value = bytes.reinterpret<ByteVar>().readBytes(length.toInt()).decodeToString()
            }
            true
        }
    }
    return value
}

val nw_browse_result_t.version get() = this.readTxtValue("version") ?: "unknown"
val nw_browse_result_t.appName get() = this.readTxtValue("app_name") ?: "unknown"
val nw_browse_result_t.appAuthor get() = this.readTxtValue("app_author") ?: "unknown"
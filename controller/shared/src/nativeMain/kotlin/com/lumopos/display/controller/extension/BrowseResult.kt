package com.lumopos.display.controller.extension

import com.lumopos.display.discovery.ScreenServiceConstants
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.reinterpret
import platform.Network.nw_browse_result_copy_txt_record_object
import platform.Network.nw_browse_result_t
import platform.Network.nw_txt_record_access_key
import platform.Network.nw_txt_record_find_key_non_empty_value
import kotlin.uuid.Uuid

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

val nw_browse_result_t.id get() = this.readTxtValue(ScreenServiceConstants.ID)?.let {
    Uuid.parse(it)
}
val nw_browse_result_t.version get() = this.readTxtValue(ScreenServiceConstants.APP_VERSION) ?: "unknown"
val nw_browse_result_t.appName get() = this.readTxtValue(ScreenServiceConstants.APP_NAME) ?: "unknown"
val nw_browse_result_t.appAuthor get() = this.readTxtValue(ScreenServiceConstants.APP_AUTHOR) ?: "unknown"
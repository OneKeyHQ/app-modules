package com.onekeyfe.reactnativelitecard.utils

import android.content.pm.ApplicationInfo
import com.margelo.nitro.nativelogger.OneKeyLog

object LogUtil {
    // Only enable detailed NFC/APDU logging in debug builds
    // Uses FLAG_DEBUGGABLE from the host app's ApplicationInfo at runtime,
    // rather than the library's BuildConfig which won't reflect the host's build type.
    private val isDebug: Boolean by lazy {
        try {
            val app = Utils.getApp()
            app != null && (app.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        } catch (_: Exception) {
            false
        }
    }

    @JvmStatic
    fun printLog(tag: String, msg: String) {
        if (isDebug) {
            OneKeyLog.debug(tag, msg)
        }
    }
}

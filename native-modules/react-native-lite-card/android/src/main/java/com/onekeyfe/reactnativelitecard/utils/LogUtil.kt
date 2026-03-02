package com.onekeyfe.reactnativelitecard.utils

import com.margelo.nitro.nativelogger.OneKeyLog

object LogUtil {
    // Only enable detailed NFC/APDU logging in debug builds
    private val isDebug: Boolean by lazy {
        try {
            val context = com.margelo.nitro.NitroModules.applicationContext
            context != null && (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
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

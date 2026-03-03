package com.onekeyfe.reactnativelitecard.utils

import com.margelo.nitro.nativelogger.OneKeyLog
import com.onekeyfe.reactnativelitecard.BuildConfig

object LogUtil {
    // Only enable detailed NFC/APDU logging in debug builds
    private val isDebug: Boolean by lazy {
        BuildConfig.DEBUG
    }

    @JvmStatic
    fun printLog(tag: String, msg: String) {
        if (isDebug) {
            OneKeyLog.debug(tag, msg)
        }
    }
}

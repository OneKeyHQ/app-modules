package com.onekeyfe.reactnativelitecard.utils

import com.margelo.nitro.nativelogger.OneKeyLog

object LogUtil {
    @JvmStatic
    fun printLog(tag: String, msg: String) {
        OneKeyLog.debug(tag, msg)
    }
}

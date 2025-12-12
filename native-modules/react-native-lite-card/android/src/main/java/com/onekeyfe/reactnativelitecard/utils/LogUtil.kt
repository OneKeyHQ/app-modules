package com.onekeyfe.reactnativelitecard.utils

import com.onekeyfe.reactnativelitecard.onekeyLite.NfcConstant
import com.onekeyfe.reactnativelitecard.LoggerManager

object LogUtil {
    @JvmStatic
    fun printLog(tag: String, msg: String) {
        if (NfcConstant.DEBUG) LoggerManager.getInstance()?.logInfo("$tag: $msg")
    }
}

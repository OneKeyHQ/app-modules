package so.onekey.app.wallet.lite.utils

import so.onekey.app.wallet.lite.onekeyLite.NfcConstant
import so.onekey.app.wallet.lite.LoggerManager

object LogUtil {
    @JvmStatic
    fun printLog(tag: String, msg: String) {
        if (NfcConstant.DEBUG) LoggerManager.getInstance()?.logInfo("$tag: $msg")
    }
}

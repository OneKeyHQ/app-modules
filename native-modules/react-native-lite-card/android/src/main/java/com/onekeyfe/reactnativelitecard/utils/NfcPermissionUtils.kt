package com.onekeyfe.reactnativelitecard.utils

import android.app.Activity
import android.os.Build

object NfcPermissionUtils {

    inline fun checkPermission(activity: Activity, doNext: () -> Unit): Int {
        return if ("xiaomi".equals(Build.MANUFACTURER, true)) {
            return checkMiuiPermission(activity, doNext)
        } else {
            doNext.invoke()
            0
        }
    }

    inline fun checkMiuiPermission(activity: Activity, doNext: () -> Unit): Int {
        when (MiUtil.checkNfcPermission(activity)) {
            MiUtil.PermissionResult.PERMISSION_GRANTED -> doNext.invoke()
            else -> {}
        }
        return MiUtil.checkNfcPermission(activity)
    }
}
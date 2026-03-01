package com.margelo.nitro.reactnativewebviewchecker

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise
import com.margelo.nitro.NitroModules
import com.margelo.nitro.nativelogger.OneKeyLog

@DoNotStrip
class ReactNativeWebviewChecker : HybridReactNativeWebviewCheckerSpec() {

    override fun getCurrentWebViewPackageInfo(): Promise<WebViewPackageInfo> {
        return Promise.async {
            val context = NitroModules.applicationContext
                ?: throw Exception("Application context unavailable")
            val pm = context.packageManager
            val pInfo = pm.getPackageInfo("com.google.android.webview", 0)
            OneKeyLog.info(
                "WebviewChecker",
                "WebView: ${pInfo.packageName} ${pInfo.versionName} ${pInfo.versionCode}"
            )
            WebViewPackageInfo(
                packageName = pInfo.packageName,
                versionName = pInfo.versionName ?: "",
                versionCode = pInfo.versionCode.toDouble()
            )
        }
    }

    override fun isGooglePlayServicesAvailable(): Promise<GooglePlayServicesStatus> {
        return Promise.async {
            val context = NitroModules.applicationContext
                ?: throw Exception("Application context unavailable")
            try {
                val googleApiAvailability =
                    com.google.android.gms.common.GoogleApiAvailability.getInstance()
                val status = googleApiAvailability.isGooglePlayServicesAvailable(context)
                val isSuccess =
                    status == com.google.android.gms.common.ConnectionResult.SUCCESS
                OneKeyLog.info(
                    "WebviewChecker",
                    "Play Services status=$status isAvailable=$isSuccess"
                )
                GooglePlayServicesStatus(
                    status = status.toDouble(),
                    isAvailable = isSuccess
                )
            } catch (e: Exception) {
                OneKeyLog.error(
                    "WebviewChecker",
                    "Play Services check failed: ${e.message}"
                )
                GooglePlayServicesStatus(status = -1.0, isAvailable = false)
            }
        }
    }
}

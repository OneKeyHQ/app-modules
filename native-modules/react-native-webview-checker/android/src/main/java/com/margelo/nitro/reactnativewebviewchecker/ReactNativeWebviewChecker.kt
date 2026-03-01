package com.margelo.nitro.reactnativewebviewchecker

import android.os.Build
import android.webkit.WebView
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

            // Use WebView.getCurrentWebViewPackage() (API 26+) to dynamically detect
            // the actual WebView provider (could be Chrome, standalone WebView, etc.)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val webViewPackage = WebView.getCurrentWebViewPackage()
                if (webViewPackage != null) {
                    OneKeyLog.info(
                        "WebviewChecker",
                        "WebView: ${webViewPackage.packageName} ${webViewPackage.versionName} ${webViewPackage.versionCode}"
                    )
                    return@async WebViewPackageInfo(
                        packageName = webViewPackage.packageName,
                        versionName = webViewPackage.versionName ?: "",
                        versionCode = webViewPackage.versionCode.toLong().toDouble()
                    )
                }
            }

            // Fallback for API < 26: try common WebView package names
            val pm = context.packageManager
            val candidates = listOf(
                "com.google.android.webview",
                "com.android.webview",
                "com.android.chrome"
            )
            for (candidate in candidates) {
                try {
                    val pInfo = pm.getPackageInfo(candidate, 0)
                    OneKeyLog.info(
                        "WebviewChecker",
                        "WebView (fallback): ${pInfo.packageName} ${pInfo.versionName} ${pInfo.versionCode}"
                    )
                    return@async WebViewPackageInfo(
                        packageName = pInfo.packageName,
                        versionName = pInfo.versionName ?: "",
                        versionCode = pInfo.versionCode.toDouble()
                    )
                } catch (_: Exception) {
                    // Try next candidate
                }
            }
            throw Exception("No WebView package found")
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

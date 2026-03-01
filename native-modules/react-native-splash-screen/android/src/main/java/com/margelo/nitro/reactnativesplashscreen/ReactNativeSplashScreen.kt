package com.margelo.nitro.reactnativesplashscreen

import android.os.Build
import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise
import com.margelo.nitro.nativelogger.OneKeyLog

@DoNotStrip
class ReactNativeSplashScreen : HybridReactNativeSplashScreenSpec() {

    override fun preventAutoHideAsync(): Promise<Boolean> {
        return Promise.async {
            OneKeyLog.info("SplashScreen", "preventAutoHideAsync OS_VERSION=${Build.VERSION.SDK_INT}")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return@async true
            }
            // For Android < 12, splash screen management is handled here
            OneKeyLog.info("SplashScreen", "preventAutoHide for legacy Android")
            true
        }
    }

    override fun hideAsync(): Promise<Boolean> {
        return Promise.async {
            OneKeyLog.info("SplashScreen", "hideAsync OS_VERSION=${Build.VERSION.SDK_INT}")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                return@async true
            }
            OneKeyLog.info("SplashScreen", "hide for legacy Android")
            true
        }
    }
}

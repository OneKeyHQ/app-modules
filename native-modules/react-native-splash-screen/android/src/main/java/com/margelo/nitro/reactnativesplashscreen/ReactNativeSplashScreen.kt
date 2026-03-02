package com.margelo.nitro.reactnativesplashscreen

import android.os.Build
import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.NitroModules
import com.margelo.nitro.core.Promise
import com.margelo.nitro.nativelogger.OneKeyLog
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Splash screen module for Android compatibility.
 *
 * - API >= 31 (Android 12+): The system AndroidX SplashScreen API handles the splash screen
 *   natively in MainActivity. Both methods are no-ops and return true immediately.
 *
 * - API < 31 (Android < 12): The system has no built-in splash screen API.
 *   MainActivity calls [SplashScreenBridge.show] during onCreate to display a view overlay
 *   matching the app's splash drawable. This module then delegates:
 *     - [preventAutoHideAsync] → [SplashScreenBridge.preventAutoHide] to keep the overlay visible
 *       until JS explicitly hides it.
 *     - [hideAsync] → [SplashScreenBridge.hide] to remove the overlay.
 *
 * iOS: expo-splash-screen via the launch storyboard handles everything. The iOS implementation
 * in ReactNativeSplashScreen.swift is intentionally empty.
 */
@DoNotStrip
class ReactNativeSplashScreen : HybridReactNativeSplashScreenSpec() {

    override fun preventAutoHideAsync(): Promise<Boolean> {
        return Promise.async {
            OneKeyLog.info("SplashScreen", "preventAutoHideAsync OS_VERSION=${Build.VERSION.SDK_INT}")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // API 31+: AndroidX SplashScreen handles it in MainActivity
                return@async true
            }
            val activity = NitroModules.applicationContext?.currentActivity
            if (activity == null) {
                OneKeyLog.warn("SplashScreen", "preventAutoHideAsync: no current activity")
                return@async false
            }
            SplashScreenBridge.preventAutoHide(activity)
            true
        }
    }

    override fun hideAsync(): Promise<Boolean> {
        return Promise.async {
            OneKeyLog.info("SplashScreen", "hideAsync OS_VERSION=${Build.VERSION.SDK_INT}")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // API 31+: AndroidX SplashScreen handles it
                return@async true
            }
            val activity = NitroModules.applicationContext?.currentActivity
            if (activity == null) {
                OneKeyLog.warn("SplashScreen", "hideAsync: no current activity")
                return@async false
            }
            val latch = CountDownLatch(1)
            var result = false
            SplashScreenBridge.hide(
                activity,
                onSuccess = { hasEffect ->
                    result = hasEffect
                    latch.countDown()
                },
                onFailure = { reason ->
                    OneKeyLog.warn("SplashScreen", "hideAsync failed: $reason")
                    result = false
                    latch.countDown()
                }
            )
            latch.await(5, TimeUnit.SECONDS)
            result
        }
    }
}

package com.rnping

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule
import java.net.InetAddress

@ReactModule(name = RNReactNativePingModule.NAME)
class RNReactNativePingModule(reactContext: ReactApplicationContext) :
    NativeRNReactNativePingSpec(reactContext) {

    companion object {
        const val NAME = "RNReactNativePing"
        private const val DEFAULT_TIMEOUT_MS = 3000
    }

    override fun getName(): String = NAME

    override fun start(ipAddress: String, option: ReadableMap, promise: Promise) {
        Thread {
            try {
                val timeout = if (option.hasKey("timeout")) option.getInt("timeout") else DEFAULT_TIMEOUT_MS
                val startTime = System.currentTimeMillis()
                val reachable = InetAddress.getByName(ipAddress).isReachable(timeout)
                val elapsed = System.currentTimeMillis() - startTime
                if (reachable) {
                    promise.resolve(elapsed.toDouble())
                } else {
                    promise.reject("PING_TIMEOUT", "Host $ipAddress is not reachable within ${timeout}ms")
                }
            } catch (e: Exception) {
                promise.reject("PING_ERROR", e.message, e)
            }
        }.start()
    }
}

package com.onekeyfe.reactnativenetworkthrottle

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = NetworkThrottleModule.NAME)
class NetworkThrottleModule(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    companion object {
        const val NAME = "OneKeyNetworkThrottle"
    }

    init {
        NetworkThrottle.install(reactContext)
    }

    override fun getName(): String = NAME

    @ReactMethod
    fun getConfig(promise: Promise) {
        promise.resolve(NetworkThrottle.getConfig())
    }

    @ReactMethod
    fun setConfig(config: ReadableMap, promise: Promise) {
        promise.resolve(NetworkThrottle.setConfig(config))
    }
}

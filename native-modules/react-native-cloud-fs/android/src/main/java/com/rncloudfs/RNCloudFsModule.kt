package com.rncloudfs

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = RNCloudFsModule.NAME)
class RNCloudFsModule(reactContext: ReactApplicationContext) :
    NativeRNCloudFsSpec(reactContext) {

    companion object {
        const val NAME = "RNCloudFs"
        private const val NOT_AVAILABLE_ERROR = "iCloud is not available on Android"
    }

    override fun getName(): String = NAME

    override fun isAvailable(promise: Promise) {
        promise.resolve(false)
    }

    override fun createFile(options: ReadableMap, promise: Promise) {
        promise.reject("NOT_AVAILABLE", NOT_AVAILABLE_ERROR)
    }

    override fun fileExists(options: ReadableMap, promise: Promise) {
        promise.reject("NOT_AVAILABLE", NOT_AVAILABLE_ERROR)
    }

    override fun listFiles(options: ReadableMap, promise: Promise) {
        promise.reject("NOT_AVAILABLE", NOT_AVAILABLE_ERROR)
    }

    override fun getIcloudDocument(filename: String, promise: Promise) {
        promise.reject("NOT_AVAILABLE", NOT_AVAILABLE_ERROR)
    }

    override fun deleteFromCloud(item: ReadableMap, promise: Promise) {
        promise.reject("NOT_AVAILABLE", NOT_AVAILABLE_ERROR)
    }

    override fun copyToCloud(options: ReadableMap, promise: Promise) {
        promise.reject("NOT_AVAILABLE", NOT_AVAILABLE_ERROR)
    }

    override fun syncCloud(promise: Promise) {
        promise.reject("NOT_AVAILABLE", NOT_AVAILABLE_ERROR)
    }
}

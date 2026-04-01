package com.backgroundthread

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule

/**
 * TurboModule entry point for BackgroundThread.
 * Delegates all heavy lifting to [BackgroundThreadManager] singleton.
 *
 * Mirrors iOS BackgroundThread.mm.
 */
@ReactModule(name = BackgroundThreadModule.NAME)
class BackgroundThreadModule(reactContext: ReactApplicationContext) :
    NativeBackgroundThreadSpec(reactContext) {

    companion object {
        const val NAME = "BackgroundThread"
    }

    override fun getName(): String = NAME

    override fun installSharedBridge() {
        BackgroundThreadManager.getInstance().installSharedBridgeInMainRuntime(reactApplicationContext)
    }

    override fun startBackgroundRunnerWithEntryURL(entryURL: String) {
        BackgroundThreadManager.getInstance().startBackgroundRunnerWithEntryURL(reactApplicationContext, entryURL)
    }

    override fun loadSegmentInBackground(segmentId: Double, path: String, promise: Promise) {
        try {
            BackgroundThreadManager.getInstance()
                .registerSegmentInBackground(segmentId.toInt(), path)
            promise.resolve(null)
        } catch (e: Exception) {
            promise.reject("BG_SEGMENT_LOAD_ERROR", e.message, e)
        }
    }
}

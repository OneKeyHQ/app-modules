package com.backgroundthread

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.UiThreadUtil
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

    /**
     * Force register event callback during initialization.
     * This is mainly to handle the scenario of restarting in development environment.
     */
    override fun initBackgroundThread() {
        bindMessageCallback()
        BackgroundThreadManager.getInstance().installSharedBridgeInMainRuntime(reactApplicationContext)
    }

    override fun startBackgroundRunnerWithEntryURL(entryURL: String) {
        val manager = BackgroundThreadManager.getInstance()
        if (!manager.checkMessageCallback()) {
            bindMessageCallback()
        }
        manager.startBackgroundRunnerWithEntryURL(reactApplicationContext, entryURL)
    }

    override fun postBackgroundMessage(message: String) {
        val manager = BackgroundThreadManager.getInstance()
        if (!manager.checkMessageCallback()) {
            bindMessageCallback()
        }
        manager.postBackgroundMessage(message)
    }

    private fun bindMessageCallback() {
        BackgroundThreadManager.getInstance().setOnMessageCallback { message ->
            UiThreadUtil.runOnUiThread {
                try {
                    emitOnBackgroundMessage(message)
                } catch (e: Exception) {
                    BTLogger.error("Error emitting background message: ${e.message}")
                }
            }
        }
    }

}

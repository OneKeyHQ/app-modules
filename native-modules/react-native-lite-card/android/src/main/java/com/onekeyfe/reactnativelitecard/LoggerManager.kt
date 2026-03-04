package com.onekeyfe.reactnativelitecard

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.margelo.nitro.nativelogger.OneKeyLog
import com.onekeyfe.reactnativelitecard.utils.sendEvent

class LoggerManager(private val context: ReactApplicationContext) :
    ReactContextBaseJavaModule(context) {

    companion object {
        private val TAG = LoggerManager::class.simpleName.toString()

        private const val LOG_EVENT_INFO = "native_log_info"

        private var instance: LoggerManager? = null

        fun getInstance(): LoggerManager? {
            return instance
        }
    }

    init {
        instance = this
    }

    override fun getName(): String = TAG

    @ReactMethod
    public fun logInfo(message: String) {
        OneKeyLog.info("LiteCard", message)
        sendEvent(context, LOG_EVENT_INFO, message)
    }
}

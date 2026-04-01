package com.splitbundleloader

/**
 * Lightweight logging wrapper that dynamically dispatches to OneKeyLog.
 * Uses reflection to avoid a hard dependency on the native-logger module.
 * Falls back to android.util.Log when OneKeyLog is not available.
 *
 * Mirrors iOS SBLLogger.
 */
object SBLLogger {
    private const val TAG = "SplitBundleLoader"

    private val logClass: Class<*>? by lazy {
        try {
            Class.forName("com.margelo.nitro.nativelogger.OneKeyLog")
        } catch (_: ClassNotFoundException) {
            null
        }
    }

    private val methods by lazy {
        val cls = logClass ?: return@lazy null
        mapOf(
            "debug" to cls.getMethod("debug", String::class.java, String::class.java),
            "info" to cls.getMethod("info", String::class.java, String::class.java),
            "warn" to cls.getMethod("warn", String::class.java, String::class.java),
            "error" to cls.getMethod("error", String::class.java, String::class.java),
        )
    }

    @JvmStatic
    fun debug(message: String) = log("debug", message, android.util.Log.DEBUG)

    @JvmStatic
    fun info(message: String) = log("info", message, android.util.Log.INFO)

    @JvmStatic
    fun warn(message: String) = log("warn", message, android.util.Log.WARN)

    @JvmStatic
    fun error(message: String) = log("error", message, android.util.Log.ERROR)

    private fun log(level: String, message: String, androidLogLevel: Int) {
        val method = methods?.get(level)
        if (method != null) {
            try {
                method.invoke(null, TAG, message)
                return
            } catch (_: Exception) {
                // Fall through to android.util.Log
            }
        }
        android.util.Log.println(androidLogLevel, TAG, message)
    }
}

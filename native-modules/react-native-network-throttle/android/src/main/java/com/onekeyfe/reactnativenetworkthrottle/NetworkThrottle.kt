package com.onekeyfe.reactnativenetworkthrottle

import android.content.Context
import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.network.OkHttpClientProvider
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

internal object NetworkThrottle {
    private const val TAG = "OneKeyNetworkThrottle"
    private const val PROFILE_SLOW_4G = "slow4g"
    private const val DEFAULT_LATENCY_MS = 562.5

    private val enabled = AtomicBoolean(false)
    private val latencyNanos = AtomicLong((DEFAULT_LATENCY_MS * 1_000_000.0).toLong())
    private val installed = AtomicBoolean(false)

    fun install(context: Context) {
        if (!installed.compareAndSet(false, true)) {
            return
        }
        val applicationContext = context.applicationContext
        OkHttpClientProvider.setOkHttpClientFactory {
            val builder: OkHttpClient.Builder =
                OkHttpClientProvider.createClientBuilder(applicationContext)
            builder.addInterceptor(LatencyInterceptor())
            builder.build()
        }
        Log.i(TAG, "[onekey-network-throttle] installed RN OkHttp latency interceptor")
    }

    fun setConfig(config: ReadableMap): WritableMap {
        val nextEnabled = config.hasKey("enabled") && config.getBoolean("enabled")
        var nextLatencyMs =
            if (config.hasKey("latencyMs")) config.getDouble("latencyMs") else DEFAULT_LATENCY_MS
        if (nextLatencyMs <= 0) {
            nextLatencyMs = DEFAULT_LATENCY_MS
        }

        enabled.set(nextEnabled)
        latencyNanos.set((nextLatencyMs * 1_000_000.0).toLong())
        Log.i(
            TAG,
            "[onekey-network-throttle] native config enabled=$nextEnabled profile=$PROFILE_SLOW_4G latencyMs=$nextLatencyMs"
        )
        return getConfig()
    }

    fun getConfig(): WritableMap {
        val map = Arguments.createMap()
        map.putBoolean("enabled", enabled.get())
        map.putString("profile", PROFILE_SLOW_4G)
        map.putDouble("latencyMs", latencyNanos.get() / 1_000_000.0)
        return map
    }

    private fun getLatencyNanos(): Long = if (enabled.get()) latencyNanos.get() else 0L

    private class LatencyInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val delayNanos = getLatencyNanos()
            if (delayNanos > 0) {
                try {
                    val delayMs = TimeUnit.NANOSECONDS.toMillis(delayNanos)
                    val remainingNanos =
                        (delayNanos - TimeUnit.MILLISECONDS.toNanos(delayMs)).toInt()
                    Thread.sleep(delayMs, remainingNanos)
                } catch (error: InterruptedException) {
                    Thread.currentThread().interrupt()
                    throw IOException("Interrupted while applying OneKey network throttle", error)
                }
            }
            return chain.proceed(chain.request())
        }
    }
}

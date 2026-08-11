package com.onekeyfe.reactnativenetworkthrottle

import android.content.Context
import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.network.OkHttpClientProvider
import java.io.IOException
import java.io.InterruptedIOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.ForwardingSink
import okio.ForwardingSource
import okio.Source
import okio.buffer

internal object NetworkThrottle {
    private const val TAG = "OneKeyNetworkThrottle"
    private const val PROFILE_SLOW_4G = "slow4g"
    private const val DEFAULT_LATENCY_MS = 562.5
    private const val DEFAULT_THROUGHPUT_BPS = 102 * 1024

    private val enabled = AtomicBoolean(false)
    private val latencyNanos = AtomicLong((DEFAULT_LATENCY_MS * 1_000_000.0).toLong())
    private val downloadBps = AtomicLong(DEFAULT_THROUGHPUT_BPS.toLong())
    private val uploadBps = AtomicLong(DEFAULT_THROUGHPUT_BPS.toLong())
    private val installed = AtomicBoolean(false)
    private val bypassUrlOrigins = AtomicReference<Set<String>>(emptySet())

    fun install(context: Context) {
        if (!installed.compareAndSet(false, true)) {
            return
        }
        val applicationContext = context.applicationContext
        OkHttpClientProvider.setOkHttpClientFactory {
            val builder: OkHttpClient.Builder =
                OkHttpClientProvider.createClientBuilder(applicationContext)
            builder.addInterceptor(ThrottleInterceptor())
            builder.build()
        }
        Log.i(TAG, "[onekey-network-throttle] installed RN OkHttp throttle interceptor")
    }

    fun setConfig(config: ReadableMap): WritableMap {
        val hasEnabled = config.hasKey("enabled") && !config.isNull("enabled")
        val hasLatencyMs = config.hasKey("latencyMs") && !config.isNull("latencyMs")
        val hasDownloadBps = config.hasKey("downloadBps") && !config.isNull("downloadBps")
        val hasUploadBps = config.hasKey("uploadBps") && !config.isNull("uploadBps")
        val nextEnabled =
            if (hasEnabled) config.getBoolean("enabled") else enabled.get()
        var nextLatencyMs =
            if (hasLatencyMs) {
                config.getDouble("latencyMs")
            } else {
                latencyNanos.get() / 1_000_000.0
            }
        if (nextLatencyMs <= 0) {
            nextLatencyMs = DEFAULT_LATENCY_MS
        }
        var nextDownloadBps =
            if (hasDownloadBps) {
                config.getDouble("downloadBps").toLong()
            } else {
                downloadBps.get()
            }
        if (nextDownloadBps <= 0) {
            nextDownloadBps = DEFAULT_THROUGHPUT_BPS.toLong()
        }
        var nextUploadBps =
            if (hasUploadBps) {
                config.getDouble("uploadBps").toLong()
            } else {
                uploadBps.get()
            }
        if (nextUploadBps <= 0) {
            nextUploadBps = DEFAULT_THROUGHPUT_BPS.toLong()
        }
        if (config.hasKey("bypassUrlOrigins") && !config.isNull("bypassUrlOrigins")) {
            val origins = config.getArray("bypassUrlOrigins")
            val normalizedOrigins = buildSet {
                if (origins != null) {
                    for (index in 0 until origins.size()) {
                        if (origins.getType(index) == ReadableType.String) {
                            normalizeOrigin(origins.getString(index))?.let(::add)
                        }
                    }
                }
            }
            bypassUrlOrigins.updateAndGet { current -> current + normalizedOrigins }
        }

        enabled.set(nextEnabled)
        latencyNanos.set((nextLatencyMs * 1_000_000.0).toLong())
        downloadBps.set(nextDownloadBps)
        uploadBps.set(nextUploadBps)
        Log.i(
            TAG,
            "[onekey-network-throttle] native config enabled=$nextEnabled profile=$PROFILE_SLOW_4G latencyMs=$nextLatencyMs downloadBps=$nextDownloadBps uploadBps=$nextUploadBps"
        )
        return getConfig()
    }

    fun getConfig(): WritableMap {
        val map = Arguments.createMap()
        map.putBoolean("enabled", enabled.get())
        map.putString("profile", PROFILE_SLOW_4G)
        map.putDouble("latencyMs", latencyNanos.get() / 1_000_000.0)
        map.putDouble("downloadBps", downloadBps.get().toDouble())
        map.putDouble("uploadBps", uploadBps.get().toDouble())
        val origins = Arguments.createArray()
        bypassUrlOrigins.get().sorted().forEach(origins::pushString)
        map.putArray("bypassUrlOrigins", origins)
        return map
    }

    private fun getLatencyNanos(): Long = if (enabled.get()) latencyNanos.get() else 0L
    private fun getDownloadBps(): Long = if (enabled.get()) downloadBps.get() else 0L
    private fun getUploadBps(): Long = if (enabled.get()) uploadBps.get() else 0L

    private fun canonicalOrigin(url: HttpUrl): String =
        HttpUrl.Builder()
            .scheme(url.scheme)
            .host(url.host)
            .port(url.port)
            .build()
            .toString()
            .removeSuffix("/")

    private fun normalizeOrigin(value: String?): String? =
        value?.toHttpUrlOrNull()?.let(::canonicalOrigin)

    private fun shouldBypass(requestUrl: HttpUrl): Boolean =
        bypassUrlOrigins.get().contains(canonicalOrigin(requestUrl))

    private fun sleepNanos(delayNanos: Long) {
        if (delayNanos <= 0) {
            return
        }
        try {
            val delayMs = TimeUnit.NANOSECONDS.toMillis(delayNanos)
            val remainingNanos =
                (delayNanos - TimeUnit.MILLISECONDS.toNanos(delayMs)).toInt()
            Thread.sleep(delayMs, remainingNanos)
        } catch (error: InterruptedException) {
            Thread.currentThread().interrupt()
            val interruptedIOException =
                InterruptedIOException("Interrupted while applying OneKey network throttle")
            interruptedIOException.initCause(error)
            throw interruptedIOException
        }
    }

    private class BandwidthLimiter(private val bytesPerSecond: Long) {
        private val startNanos = System.nanoTime()
        private var transferredBytes = 0L

        fun throttle(byteCount: Long) {
            if (bytesPerSecond <= 0 || byteCount <= 0) {
                return
            }
            transferredBytes += byteCount
            val expectedElapsedNanos =
                (transferredBytes.toDouble() * 1_000_000_000.0 / bytesPerSecond.toDouble())
                    .toLong()
            val elapsedNanos = System.nanoTime() - startNanos
            sleepNanos(expectedElapsedNanos - elapsedNanos)
        }
    }

    private class ThrottledRequestBody(
        private val delegate: RequestBody,
        private val bytesPerSecond: Long
    ) : RequestBody() {
        override fun contentType(): MediaType? = delegate.contentType()

        override fun contentLength(): Long = delegate.contentLength()

        override fun isDuplex(): Boolean = delegate.isDuplex()

        override fun isOneShot(): Boolean = delegate.isOneShot()

        override fun writeTo(sink: BufferedSink) {
            val limiter = BandwidthLimiter(bytesPerSecond)
            val throttledSink = object : ForwardingSink(sink) {
                override fun write(source: Buffer, byteCount: Long) {
                    super.write(source, byteCount)
                    limiter.throttle(byteCount)
                }
            }.buffer()
            delegate.writeTo(throttledSink)
            throttledSink.flush()
        }
    }

    private class ThrottledResponseBody(
        private val delegate: ResponseBody,
        private val bytesPerSecond: Long
    ) : ResponseBody() {
        private var bufferedSource: BufferedSource? = null

        override fun contentType(): MediaType? = delegate.contentType()

        override fun contentLength(): Long = delegate.contentLength()

        override fun source(): BufferedSource {
            if (bufferedSource == null) {
                val limiter = BandwidthLimiter(bytesPerSecond)
                val throttledSource: Source = object : ForwardingSource(delegate.source()) {
                    override fun read(sink: Buffer, byteCount: Long): Long {
                        val bytesRead = super.read(sink, byteCount)
                        if (bytesRead > 0) {
                            limiter.throttle(bytesRead)
                        }
                        return bytesRead
                    }
                }
                bufferedSource = throttledSource.buffer()
            }
            return bufferedSource!!
        }
    }

    private class ThrottleInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            if (shouldBypass(request.url)) {
                return chain.proceed(request)
            }
            val requestStartNanos = System.nanoTime()
            val delayNanos = getLatencyNanos()
            val requestBody = request.body
            val activeUploadBps = getUploadBps()
            val throttledRequest =
                if (activeUploadBps > 0 && requestBody != null) {
                    request.newBuilder()
                        .method(
                            request.method,
                            ThrottledRequestBody(requestBody, activeUploadBps)
                        )
                        .build()
                } else {
                    request
                }
            val response = chain.proceed(throttledRequest)
            if (delayNanos > 0) {
                try {
                    val elapsedNanos = System.nanoTime() - requestStartNanos
                    val remainingDelayNanos = delayNanos - elapsedNanos
                    if (remainingDelayNanos <= 0) {
                        return wrapResponseBody(response)
                    }
                    sleepNanos(remainingDelayNanos)
                } catch (error: InterruptedIOException) {
                    response.close()
                    throw IOException("Interrupted while applying OneKey network throttle", error)
                }
            }
            return wrapResponseBody(response)
        }

        private fun wrapResponseBody(response: Response): Response {
            val responseBody = response.body ?: return response
            val activeDownloadBps = getDownloadBps()
            if (activeDownloadBps <= 0) {
                return response
            }
            return response.newBuilder()
                .body(ThrottledResponseBody(responseBody, activeDownloadBps))
                .build()
        }
    }
}

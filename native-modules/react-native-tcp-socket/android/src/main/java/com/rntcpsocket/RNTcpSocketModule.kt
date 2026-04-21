package com.rntcpsocket

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import java.net.InetSocketAddress
import java.net.Socket

@ReactModule(name = RNTcpSocketModule.NAME)
class RNTcpSocketModule(reactContext: ReactApplicationContext) :
    NativeTcpSocketSpec(reactContext) {

    companion object {
        const val NAME = "RNTcpSocket"
    }

    override fun getName(): String = NAME

    override fun connectWithTimeout(host: String, port: Double, timeoutMs: Double, promise: Promise) {
        Thread {
            try {
                val portInt = port.toInt()
                val timeout = timeoutMs.toInt()
                val startTime = System.currentTimeMillis()
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(host, portInt), timeout)
                }
                val elapsed = System.currentTimeMillis() - startTime
                promise.resolve(elapsed.toDouble())
            } catch (e: Exception) {
                promise.reject("TCP_SOCKET_ERROR", e.message, e)
            }
        }.start()
    }
}

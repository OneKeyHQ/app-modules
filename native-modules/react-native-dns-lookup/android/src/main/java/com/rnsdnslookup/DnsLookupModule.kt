package com.rnsdnslookup

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.module.annotations.ReactModule
import java.net.InetAddress

@ReactModule(name = DnsLookupModule.NAME)
class DnsLookupModule(reactContext: ReactApplicationContext) :
    NativeRNDnsLookupSpec(reactContext) {

    companion object {
        const val NAME = "RNDnsLookup"
    }

    override fun getName(): String = NAME

    override fun getIpAddresses(hostname: String, promise: Promise) {
        Thread {
            try {
                val addresses = InetAddress.getAllByName(hostname)
                val result = WritableNativeArray()
                for (address in addresses) {
                    result.pushString(address.hostAddress)
                }
                promise.resolve(result)
            } catch (e: Exception) {
                promise.reject("DNS_LOOKUP_ERROR", e.message, e)
            }
        }.start()
    }
}

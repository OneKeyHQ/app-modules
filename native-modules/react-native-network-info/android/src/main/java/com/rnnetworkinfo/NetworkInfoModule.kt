package com.rnnetworkinfo

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface

@ReactModule(name = NetworkInfoModule.NAME)
class NetworkInfoModule(reactContext: ReactApplicationContext) :
    NativeRNNetworkInfoSpec(reactContext) {

    companion object {
        const val NAME = "RNNetworkInfo"
    }

    override fun getName(): String = NAME

    // MARK: - getSSID

    override fun getSSID(promise: Promise) {
        Thread {
            try {
                val ssid = getWifiSSID()
                promise.resolve(ssid)
            } catch (e: Exception) {
                promise.resolve(null)
            }
        }.start()
    }

    private fun getWifiSSID(): String? {
        val context = reactApplicationContext
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            ?: return null
        @Suppress("DEPRECATION")
        val wifiInfo: WifiInfo = wifiManager.connectionInfo ?: return null
        @Suppress("DEPRECATION")
        val ssid = wifiInfo.ssid
        if (ssid == null || ssid == "<unknown ssid>") return null
        return if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid.substring(1, ssid.length - 1)
        } else {
            ssid
        }
    }

    // MARK: - getBSSID

    override fun getBSSID(promise: Promise) {
        Thread {
            try {
                val context = reactApplicationContext
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
                @Suppress("DEPRECATION")
                val bssid = wifiManager?.connectionInfo?.bssid
                promise.resolve(bssid)
            } catch (e: Exception) {
                promise.resolve(null)
            }
        }.start()
    }

    // MARK: - getBroadcast

    override fun getBroadcast(promise: Promise) {
        Thread {
            try {
                var broadcast: String? = null
                val interfaces = NetworkInterface.getNetworkInterfaces()
                while (interfaces.hasMoreElements()) {
                    val iface = interfaces.nextElement()
                    if (iface.name == "wlan0" || iface.name == "eth0") {
                        for (ia in iface.interfaceAddresses) {
                            val broadcastAddr = ia.broadcast
                            if (broadcastAddr != null) {
                                broadcast = broadcastAddr.hostAddress
                                break
                            }
                        }
                    }
                    if (broadcast != null) break
                }
                promise.resolve(broadcast)
            } catch (e: Exception) {
                promise.resolve(null)
            }
        }.start()
    }

    // MARK: - getIPAddress

    override fun getIPAddress(promise: Promise) {
        Thread {
            try {
                val address = getIPv4Address()
                promise.resolve(address ?: "")
            } catch (e: Exception) {
                promise.resolve("")
            }
        }.start()
    }

    // MARK: - getIPV6Address

    override fun getIPV6Address(promise: Promise) {
        Thread {
            try {
                var address: String? = null
                val interfaces = NetworkInterface.getNetworkInterfaces()
                loop@ while (interfaces.hasMoreElements()) {
                    val iface = interfaces.nextElement()
                    if (!iface.isUp || iface.isLoopback) continue
                    val addrs = iface.inetAddresses
                    while (addrs.hasMoreElements()) {
                        val addr = addrs.nextElement()
                        if (addr is Inet6Address && !addr.isLoopbackAddress && !addr.isLinkLocalAddress) {
                            address = addr.hostAddress
                            break@loop
                        }
                    }
                }
                promise.resolve(address ?: "")
            } catch (e: Exception) {
                promise.resolve("")
            }
        }.start()
    }

    // MARK: - getGatewayIPAddress

    override fun getGatewayIPAddress(promise: Promise) {
        Thread {
            try {
                val context = reactApplicationContext
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
                @Suppress("DEPRECATION")
                val dhcpInfo = wifiManager?.dhcpInfo
                val gateway = if (dhcpInfo != null && dhcpInfo.gateway != 0) {
                    formatIpAddress(dhcpInfo.gateway)
                } else {
                    null
                }
                promise.resolve(gateway ?: "")
            } catch (e: Exception) {
                promise.resolve("")
            }
        }.start()
    }

    // MARK: - getIPV4Address

    override fun getIPV4Address(promise: Promise) {
        Thread {
            try {
                val address = getIPv4Address()
                promise.resolve(address ?: "0.0.0.0")
            } catch (e: Exception) {
                promise.resolve("0.0.0.0")
            }
        }.start()
    }

    // MARK: - getWIFIIPV4Address

    override fun getWIFIIPV4Address(promise: Promise) {
        Thread {
            try {
                var address: String? = null
                val interfaces = NetworkInterface.getNetworkInterfaces()
                loop@ while (interfaces.hasMoreElements()) {
                    val iface = interfaces.nextElement()
                    if (iface.name == "wlan0") {
                        val addrs = iface.inetAddresses
                        while (addrs.hasMoreElements()) {
                            val addr = addrs.nextElement()
                            if (addr is Inet4Address && !addr.isLoopbackAddress) {
                                address = addr.hostAddress
                                break@loop
                            }
                        }
                    }
                }
                promise.resolve(address ?: "0.0.0.0")
            } catch (e: Exception) {
                promise.resolve("0.0.0.0")
            }
        }.start()
    }

    // MARK: - getSubnet

    override fun getSubnet(promise: Promise) {
        Thread {
            try {
                var subnet: String? = null
                val interfaces = NetworkInterface.getNetworkInterfaces()
                loop@ while (interfaces.hasMoreElements()) {
                    val iface = interfaces.nextElement()
                    if (!iface.isUp || iface.isLoopback) continue
                    for (ia in iface.interfaceAddresses) {
                        val addr = ia.address
                        if (addr is Inet4Address && !addr.isLoopbackAddress) {
                            val prefixLen = ia.networkPrefixLength.toInt()
                            val mask = prefixLengthToSubnetMask(prefixLen)
                            subnet = mask
                            break@loop
                        }
                    }
                }
                promise.resolve(subnet ?: "0.0.0.0")
            } catch (e: Exception) {
                promise.resolve("0.0.0.0")
            }
        }.start()
    }

    // MARK: - Helpers

    private fun getIPv4Address(): String? {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val iface = interfaces.nextElement()
            if (!iface.isUp || iface.isLoopback) continue
            val addrs = iface.inetAddresses
            while (addrs.hasMoreElements()) {
                val addr = addrs.nextElement()
                if (addr is Inet4Address && !addr.isLoopbackAddress) {
                    return addr.hostAddress
                }
            }
        }
        return null
    }

    @Suppress("DEPRECATION")
    private fun formatIpAddress(ip: Int): String {
        return String.format(
            "%d.%d.%d.%d",
            ip and 0xff,
            ip shr 8 and 0xff,
            ip shr 16 and 0xff,
            ip shr 24 and 0xff
        )
    }

    private fun prefixLengthToSubnetMask(prefixLength: Int): String {
        val mask = if (prefixLength == 0) 0 else (-1 shl (32 - prefixLength))
        return String.format(
            "%d.%d.%d.%d",
            mask shr 24 and 0xff,
            mask shr 16 and 0xff,
            mask shr 8 and 0xff,
            mask and 0xff
        )
    }
}

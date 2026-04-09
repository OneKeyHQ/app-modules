package com.rnnetworkinfo

import android.content.Context
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.InterfaceAddress
import java.net.NetworkInterface

/**
 * Ported from upstream react-native-network-info RNNetworkInfo.java.
 * Adapted to extend NativeRNNetworkInfoSpec (TurboModule).
 */
@ReactModule(name = NetworkInfoModule.NAME)
class NetworkInfoModule(reactContext: ReactApplicationContext) :
    NativeNetworkInfoSpec(reactContext) {

    companion object {
        const val NAME = "RNNetworkInfo"

        val DSLITE_LIST = listOf(
            "192.0.0.0", "192.0.0.1", "192.0.0.2", "192.0.0.3",
            "192.0.0.4", "192.0.0.5", "192.0.0.6", "192.0.0.7"
        )
    }

    private val wifi: WifiManager =
        reactContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    override fun getName(): String = NAME

    // MARK: - getSSID

    override fun getSSID(promise: Promise) {
        Thread {
            try {
                @Suppress("DEPRECATION")
                val info: WifiInfo = wifi.connectionInfo
                var ssid: String? = null
                if (info.supplicantState == SupplicantState.COMPLETED) {
                    @Suppress("DEPRECATION")
                    ssid = info.ssid
                    if (ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                        ssid = ssid.substring(1, ssid.length - 1)
                    }
                }
                promise.resolve(ssid)
            } catch (e: Exception) {
                promise.resolve(null)
            }
        }.start()
    }

    // MARK: - getBSSID

    override fun getBSSID(promise: Promise) {
        Thread {
            try {
                @Suppress("DEPRECATION")
                val info: WifiInfo = wifi.connectionInfo
                var bssid: String? = null
                if (info.supplicantState == SupplicantState.COMPLETED) {
                    @Suppress("DEPRECATION")
                    bssid = wifi.connectionInfo.bssid
                }
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
                var ipAddress: String? = null
                for (address in getInetAddresses()) {
                    if (!address.address.isLoopbackAddress) {
                        val broadCast: InetAddress? = address.broadcast
                        if (broadCast != null) {
                            ipAddress = broadCast.toString()
                        }
                    }
                }
                promise.resolve(ipAddress)
            } catch (e: Exception) {
                promise.resolve(null)
            }
        }.start()
    }

    // MARK: - getIPAddress

    override fun getIPAddress(promise: Promise) {
        Thread {
            try {
                var ipAddress: String? = null
                var tmp = "0.0.0.0"
                for (address in getInetAddresses()) {
                    if (!address.address.isLoopbackAddress) {
                        tmp = address.address.hostAddress.toString()
                        if (!inDSLITERange(tmp)) {
                            ipAddress = tmp
                        }
                    }
                }
                promise.resolve(ipAddress)
            } catch (e: Exception) {
                promise.resolve(null)
            }
        }.start()
    }

    // MARK: - getIPV4Address

    override fun getIPV4Address(promise: Promise) {
        Thread {
            try {
                var ipAddress: String? = null
                var tmp = "0.0.0.0"
                for (address in getInetAddresses()) {
                    if (!address.address.isLoopbackAddress && address.address is Inet4Address) {
                        tmp = address.address.hostAddress.toString()
                        if (!inDSLITERange(tmp)) {
                            ipAddress = tmp
                        }
                    }
                }
                promise.resolve(ipAddress)
            } catch (e: Exception) {
                promise.resolve(null)
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

    // MARK: - getWIFIIPV4Address

    override fun getWIFIIPV4Address(promise: Promise) {
        Thread {
            try {
                @Suppress("DEPRECATION")
                val info: WifiInfo = wifi.connectionInfo
                val ipAddress = info.ipAddress
                val stringIp = String.format(
                    "%d.%d.%d.%d",
                    ipAddress and 0xff,
                    ipAddress shr 8 and 0xff,
                    ipAddress shr 16 and 0xff,
                    ipAddress shr 24 and 0xff
                )
                promise.resolve(stringIp)
            } catch (e: Exception) {
                promise.resolve(null)
            }
        }.start()
    }

    // MARK: - getSubnet

    override fun getSubnet(promise: Promise) {
        Thread {
            try {
                val interfaces = NetworkInterface.getNetworkInterfaces()
                while (interfaces.hasMoreElements()) {
                    val iface = interfaces.nextElement()
                    if (iface.isLoopback || !iface.isUp) continue

                    val addresses = iface.inetAddresses
                    for (address in iface.interfaceAddresses) {
                        val addr = addresses.nextElement()
                        if (addr is Inet6Address) continue

                        promise.resolve(intToIP(address.networkPrefixLength.toInt()))
                        return@Thread
                    }
                }
                promise.resolve("0.0.0.0")
            } catch (e: Exception) {
                promise.resolve("0.0.0.0")
            }
        }.start()
    }

    // MARK: - getGatewayIPAddress

    override fun getGatewayIPAddress(promise: Promise) {
        Thread {
            try {
                @Suppress("DEPRECATION")
                val dhcpInfo = wifi.dhcpInfo
                val gatewayIPInt = dhcpInfo.gateway
                val gatewayIP = String.format(
                    "%d.%d.%d.%d",
                    gatewayIPInt and 0xFF,
                    gatewayIPInt shr 8 and 0xFF,
                    gatewayIPInt shr 16 and 0xFF,
                    gatewayIPInt shr 24 and 0xFF
                )
                promise.resolve(gatewayIP)
            } catch (e: Exception) {
                promise.resolve(null)
            }
        }.start()
    }

    // MARK: - getFrequency

    override fun getFrequency(promise: Promise) {
        Thread {
            try {
                @Suppress("DEPRECATION")
                val info: WifiInfo = wifi.connectionInfo
                val frequency = info.frequency.toDouble()
                promise.resolve(frequency)
            } catch (e: Exception) {
                promise.resolve(null)
            }
        }.start()
    }

    // --- Private helpers (ported from upstream RNNetworkInfo.java) ---

    private fun intToIP(ip: Int): String {
        val finl = arrayOf("", "", "", "")
        var k = 1
        for (i in 0 until 4) {
            for (j in 0 until 8) {
                if (k <= ip) {
                    finl[i] += "1"
                } else {
                    finl[i] += "0"
                }
                k++
            }
        }
        return "${Integer.parseInt(finl[0], 2)}.${Integer.parseInt(finl[1], 2)}.${Integer.parseInt(finl[2], 2)}.${Integer.parseInt(finl[3], 2)}"
    }

    private fun inDSLITERange(ip: String): Boolean {
        return DSLITE_LIST.contains(ip)
    }

    private fun getInetAddresses(): List<InterfaceAddress> {
        val addresses = mutableListOf<InterfaceAddress>()
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                for (interfaceAddress in intf.interfaceAddresses) {
                    addresses.add(interfaceAddress)
                }
            }
        } catch (ex: Exception) {
            android.util.Log.e(NAME, ex.toString())
        }
        return addresses
    }
}

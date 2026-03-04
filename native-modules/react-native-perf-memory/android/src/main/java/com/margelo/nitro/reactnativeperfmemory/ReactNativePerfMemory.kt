package com.margelo.nitro.reactnativeperfmemory

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise
import com.margelo.nitro.NitroModules
import com.margelo.nitro.nativelogger.OneKeyLog
import java.io.BufferedReader
import java.io.FileReader

@DoNotStrip
class ReactNativePerfMemory : HybridReactNativePerfMemorySpec() {

    override fun getMemoryUsage(): Promise<MemoryUsage> {
        return Promise.async {
            val rssBytes = readVmRssBytesFromProcStatus()

            val context = NitroModules.applicationContext
            val am = context?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager

            if (am == null) {
                if (rssBytes != null) {
                    return@async MemoryUsage(rss = rssBytes.toDouble())
                }
                OneKeyLog.warn("PerfMemory", "ActivityManager unavailable and /proc/self/status unreadable")
                return@async MemoryUsage(rss = 0.0)
            }

            val pid = android.os.Process.myPid()
            val memInfos = am.getProcessMemoryInfo(intArrayOf(pid))

            if (memInfos == null || memInfos.isEmpty()) {
                if (rssBytes != null) {
                    return@async MemoryUsage(rss = rssBytes.toDouble())
                }
                return@async MemoryUsage(rss = 0.0)
            }

            // totalPss is in KB
            val pssBytes = memInfos[0].totalPss.toLong() * 1024L
            val finalRss = rssBytes ?: pssBytes

            MemoryUsage(rss = finalRss.toDouble())
        }
    }

    private fun readVmRssBytesFromProcStatus(): Long? {
        try {
            BufferedReader(FileReader("/proc/self/status")).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    if (!line!!.startsWith("VmRSS:")) continue
                    // Example: "VmRSS:\t  123456 kB"
                    val parts = line!!.trim().split("\\s+".toRegex())
                    if (parts.size < 2) return null
                    val kb = parts[1].toLong()
                    return kb * 1024L
                }
            }
        } catch (_: Exception) {
            // ignore
        }
        return null
    }
}

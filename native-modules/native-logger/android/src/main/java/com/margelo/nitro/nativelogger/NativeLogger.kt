package com.margelo.nitro.nativelogger

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise
import java.io.File

@DoNotStrip
class NativeLogger : HybridNativeLoggerSpec() {

    override fun write(level: Double, msg: String) {
        when (level.toInt()) {
            0 -> OneKeyLog.debug("JS", msg)
            1 -> OneKeyLog.info("JS", msg)
            2 -> OneKeyLog.warn("JS", msg)
            3 -> OneKeyLog.error("JS", msg)
            else -> OneKeyLog.info("JS", msg)
        }
    }

    override fun getLogFilePaths(): Promise<Array<String>> {
        return Promise.async {
            val dir = OneKeyLog.logsDirectory
            val files = File(dir).listFiles { _, name -> name.endsWith(".log") }
            if (files == null) {
                OneKeyLog.warn("NativeLogger", "Failed to list log directory: $dir")
                return@async arrayOf<String>()
            }
            files.sortedBy { it.name }
                .map { it.absolutePath }.toTypedArray()
        }
    }

    override fun deleteLogFiles(): Promise<Unit> {
        return Promise.async {
            val dir = OneKeyLog.logsDirectory
            val files = File(dir).listFiles { _, name -> name.endsWith(".log") }
            if (files == null) {
                OneKeyLog.warn("NativeLogger", "Failed to list log directory for deletion: $dir")
                return@async
            }
            // Skip the active log file to avoid breaking logback's open file handle
            files.filter { it.name != "app-latest.log" }.forEach { file ->
                if (!file.delete()) {
                    OneKeyLog.warn("NativeLogger", "Failed to delete log file: ${file.name}")
                }
            }
        }
    }
}

package com.margelo.nitro.nativelogger

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise
import java.io.File

@DoNotStrip
class NativeLogger : HybridNativeLoggerSpec() {

    companion object {
        /** Patterns that should never be written to log files */
        private val sensitivePatterns = listOf(
            // Hex-encoded private keys (64 hex chars)
            Regex("[0-9a-fA-F]{64}"),
            // BIP39 mnemonic-like sequences (12+ words separated by spaces)
            Regex("(?:\\b[a-z]{3,8}\\b\\s+){11,}\\b[a-z]{3,8}\\b"),
            // Base64 encoded data that looks like keys (44+ chars)
            Regex("(?:eyJ|AAAA)[A-Za-z0-9+/=]{40,}"),
        )

        fun sanitize(message: String): String {
            var result = message
            for (pattern in sensitivePatterns) {
                result = pattern.replace(result, "[REDACTED]")
            }
            // Strip newlines to prevent log injection
            result = result.replace("\n", " ").replace("\r", " ")
            return result
        }
    }

    override fun write(level: Double, msg: String) {
        val sanitized = sanitize(msg)
        when (level.toInt()) {
            0 -> OneKeyLog.debug("JS", sanitized)
            1 -> OneKeyLog.info("JS", sanitized)
            2 -> OneKeyLog.warn("JS", sanitized)
            3 -> OneKeyLog.error("JS", sanitized)
            else -> OneKeyLog.info("JS", sanitized)
        }
    }

    override fun getLogFilePaths(): Promise<Array<String>> {
        return Promise.async {
            // Flush buffered log data so the active file reflects all written logs
            OneKeyLog.flush()
            val dir = OneKeyLog.logsDirectory
            if (dir.isEmpty()) return@async arrayOf<String>()
            val files = File(dir).listFiles { _, name -> name.endsWith(".log") }
            if (files == null) {
                OneKeyLog.warn("NativeLogger", "Failed to list log directory")
                return@async arrayOf<String>()
            }
            // Return filenames only, not absolute paths
            files.sortedBy { it.name }
                .map { it.name }.toTypedArray()
        }
    }

    override fun deleteLogFiles(): Promise<Unit> {
        return Promise.async {
            val dir = OneKeyLog.logsDirectory
            if (dir.isEmpty()) return@async
            val files = File(dir).listFiles { _, name -> name.endsWith(".log") }
            if (files == null) {
                OneKeyLog.warn("NativeLogger", "Failed to list log directory for deletion")
                return@async
            }
            // Skip the active log file to avoid breaking logback's open file handle
            files.filter { it.name != "app-latest.log" }.forEach { file ->
                if (!file.delete()) {
                    OneKeyLog.warn("NativeLogger", "Failed to delete log file")
                }
            }
        }
    }
}

package com.margelo.nitro.nativelogger

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise
import java.io.File
import java.io.RandomAccessFile

@DoNotStrip
class NativeLogger : HybridNativeLoggerSpec() {

    companion object {
        /** Patterns that should never be written to log files */
        private val sensitivePatterns = listOf(
            // Hex-encoded private keys (64 hex chars), with optional 0x prefix
            Regex("(?:0x)?[0-9a-fA-F]{64}"),
            // WIF private keys (base58, starting with 5, K, or L)
            Regex("\\b[5KL][1-9A-HJ-NP-Za-km-z]{50,51}\\b"),
            // Extended keys (xprv/xpub/zprv/zpub/yprv/ypub)
            Regex("\\b[xyzXYZ](?:prv|pub)[1-9A-HJ-NP-Za-km-z]{107,108}\\b"),
            // BIP39 mnemonic-like sequences (12+ words of 3-8 lowercase letters)
            Regex("(?:\\b[a-z]{3,8}\\b[\\s,]+){11,}\\b[a-z]{3,8}\\b"),
            // Bearer/API tokens
            Regex("(?:Bearer|token[=:]?)\\s*[A-Za-z0-9_.\\-+/=]{20,}"),
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

    override fun flushPendingRepeat() {
        OneKeyLog.flushPendingRepeat()
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

    override fun getLogDirectory(): String {
        return OneKeyLog.logsDirectory
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
            OneKeyLog.flush()
            val dir = OneKeyLog.logsDirectory
            if (dir.isEmpty()) return@async
            val files = File(dir).listFiles { _, name -> name.endsWith(".log") }
            if (files == null) {
                OneKeyLog.warn("NativeLogger", "Failed to list log directory for deletion")
                return@async
            }
            files.forEach { file ->
                try {
                    if (file.name == "app-latest.log") {
                        RandomAccessFile(file, "rw").use { raf ->
                            raf.setLength(0L)
                        }
                    } else if (!file.delete()) {
                        OneKeyLog.warn("NativeLogger", "Failed to delete log file")
                    }
                } catch (_: Exception) {
                    OneKeyLog.warn("NativeLogger", "Failed to delete log file")
                }
            }
        }
    }
}

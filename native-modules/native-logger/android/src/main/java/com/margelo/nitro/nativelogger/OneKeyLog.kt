package com.margelo.nitro.nativelogger

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy
import ch.qos.logback.core.util.FileSize
import com.margelo.nitro.NitroModules
import java.io.File
import java.nio.charset.Charset
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object OneKeyLog {
    private const val APPENDER_NAME = "OneKeyFileAppender"
    private const val LOG_PREFIX = "app"
    private const val MAX_MESSAGE_LENGTH = 4096
    private const val MAX_FILE_SIZE = 20L * 1024 * 1024       // 20 MB
    private const val MAX_HISTORY = 6
    private const val TOTAL_SIZE_CAP = MAX_FILE_SIZE * MAX_HISTORY

    // Cached value; empty string means context was not yet available (will retry)
    @Volatile
    private var cachedLogsDir: String? = null

    /**
     * Initialise OneKeyLog with an Android Context before NitroModules is ready.
     * Call this early in Application.onCreate() so that logs emitted before
     * React Native loads are not silently dropped.
     */
    @JvmStatic
    fun init(context: android.content.Context) {
        if (cachedLogsDir == null) {
            cachedLogsDir = "${context.cacheDir.absolutePath}/logs"
        }
    }

    val logsDirectory: String
        get() {
            cachedLogsDir?.let { return it }
            val context = NitroModules.applicationContext
            if (context == null) {
                android.util.Log.w("OneKeyLog", "applicationContext not yet available")
                return ""  // Don't cache — allow retry on next access
            }
            val dir = "${context.cacheDir.absolutePath}/logs"
            cachedLogsDir = dir
            return dir
        }

    // Cached logger; null means not yet initialized (will retry).
    // Once successfully initialized, stays non-null.
    @Volatile
    private var cachedLogger: Logger? = null

    private val logger: Logger?
        get() {
            cachedLogger?.let { return it }
            return synchronized(this) {
                cachedLogger?.let { return@synchronized it }
                val dir = logsDirectory
                if (dir.isEmpty()) return@synchronized null

                File(dir).mkdirs()

                val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext

                val appender = RollingFileAppender<ILoggingEvent>().apply {
                    context = loggerContext
                    name = APPENDER_NAME
                    file = "$dir/$LOG_PREFIX-latest.log"
                }

                SizeAndTimeBasedRollingPolicy<ILoggingEvent>().apply {
                    context = loggerContext
                    fileNamePattern = "$dir/$LOG_PREFIX-%d{yyyy-MM-dd}.%i.log"
                    setMaxFileSize(FileSize(MAX_FILE_SIZE))
                    maxHistory = MAX_HISTORY
                    setTotalSizeCap(FileSize(TOTAL_SIZE_CAP))
                    setParent(appender)
                    start()
                    appender.rollingPolicy = this
                }

                PatternLayoutEncoder().apply {
                    context = loggerContext
                    charset = Charset.forName("UTF-8")
                    pattern = "%msg%n"
                    start()
                    appender.encoder = this
                }

                appender.start()

                // Attach appender to a dedicated named logger only (not ROOT)
                // to avoid capturing third-party library SLF4J output
                val onekeyLogger = loggerContext.getLogger("OneKey")
                onekeyLogger.level = Level.DEBUG
                onekeyLogger.getAppender(APPENDER_NAME)?.stop()
                onekeyLogger.detachAppender(APPENDER_NAME)
                onekeyLogger.addAppender(appender)
                onekeyLogger.isAdditive = false  // Do not propagate to ROOT logger

                cachedLogger = onekeyLogger
                onekeyLogger
            }
        }

    // DateTimeFormatter is immutable and thread-safe (unlike SimpleDateFormat)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.US)

    private fun truncate(message: String): String {
        return if (message.length > MAX_MESSAGE_LENGTH) {
            message.substring(0, MAX_MESSAGE_LENGTH) + "...(truncated)"
        } else {
            message
        }
    }

    private fun sanitizeForLog(str: String): String {
        return str.replace("\n", " ").replace("\r", " ")
    }

    /** Sanitize sensitive data from all log messages (native and JS) */
    private val nativeSensitivePatterns = listOf(
        Regex("(?:0x)?[0-9a-fA-F]{64}"),
        Regex("\\b[5KL][1-9A-HJ-NP-Za-km-z]{50,51}\\b"),
        Regex("\\b[xyzXYZ](?:prv|pub)[1-9A-HJ-NP-Za-km-z]{107,108}\\b"),
        Regex("(?:\\b[a-z]{3,8}\\b[\\s,]+){11,}\\b[a-z]{3,8}\\b"),
        Regex("(?:Bearer|token[=:]?)\\s*[A-Za-z0-9_.\\-+/=]{20,}"),
        Regex("(?:eyJ|AAAA)[A-Za-z0-9+/=]{40,}"),
    )

    private fun sanitizeSensitive(message: String): String {
        var result = message
        for (pattern in nativeSensitivePatterns) {
            result = pattern.replace(result, "[REDACTED]")
        }
        return result
    }

    private fun formatMessage(tag: String, level: String, message: String): String {
        val safeTag = sanitizeForLog(tag.take(64))
        val safeMessage = sanitizeSensitive(sanitizeForLog(message))
        if (safeTag == "JS") {
            return truncate(safeMessage)
        }
        val time = LocalTime.now().format(timeFormatter)
        return truncate("$time | $level : [$safeTag] $safeMessage")
    }

    private fun log(tag: String, level: String, message: String, androidLogLevel: Int) {
        val formatted = formatMessage(tag, level, message)
        val l = logger
        if (l != null) {
            when (androidLogLevel) {
                android.util.Log.DEBUG -> l.debug(formatted)
                android.util.Log.INFO -> l.info(formatted)
                android.util.Log.WARN -> l.warn(formatted)
                android.util.Log.ERROR -> l.error(formatted)
            }
        } else {
            // Fallback to android.util.Log when file logger is unavailable
            // Use formatted (sanitized) message, not raw message
            android.util.Log.println(androidLogLevel, "OneKey/${tag.take(64)}", formatted)
        }
    }

    /**
     * Flush all buffered log data to disk.
     * Call before reading log files to ensure the active file is up-to-date.
     */
    @JvmStatic
    fun flush() {
        val loggerContext = LoggerFactory.getILoggerFactory()
        if (loggerContext is LoggerContext) {
            val appender = loggerContext.getLogger("OneKey")
                ?.getAppender(APPENDER_NAME)
            if (appender is RollingFileAppender<*>) {
                try {
                    appender.outputStream?.flush()
                } catch (_: Exception) {
                    // Ignore flush errors
                }
            }
        }
    }

    @JvmStatic
    fun debug(tag: String, message: String) { log(tag, "DEBUG", message, android.util.Log.DEBUG) }

    @JvmStatic
    fun info(tag: String, message: String) { log(tag, "INFO", message, android.util.Log.INFO) }

    @JvmStatic
    fun warn(tag: String, message: String) { log(tag, "WARN", message, android.util.Log.WARN) }

    @JvmStatic
    fun error(tag: String, message: String) { log(tag, "ERROR", message, android.util.Log.ERROR) }
}

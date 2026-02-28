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
import java.text.SimpleDateFormat
import java.util.Date
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

    private val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.US)

    private fun truncate(message: String): String {
        return if (message.length > MAX_MESSAGE_LENGTH) {
            message.substring(0, MAX_MESSAGE_LENGTH) + "...(truncated)"
        } else {
            message
        }
    }

    private fun formatMessage(tag: String, level: String, message: String): String {
        if (tag == "JS") {
            return truncate(message)
        }
        val time = timeFormatter.format(Date())
        return truncate("$time | $level : [$tag] $message")
    }

    @JvmStatic
    fun debug(tag: String, message: String) { logger?.debug(formatMessage(tag, "DEBUG", message)) }

    @JvmStatic
    fun info(tag: String, message: String) { logger?.info(formatMessage(tag, "INFO", message)) }

    @JvmStatic
    fun warn(tag: String, message: String) { logger?.warn(formatMessage(tag, "WARN", message)) }

    @JvmStatic
    fun error(tag: String, message: String) { logger?.error(formatMessage(tag, "ERROR", message)) }
}

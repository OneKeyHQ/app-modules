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

object OneKeyLog {
    private const val APPENDER_NAME = "OneKeyFileAppender"
    private const val LOG_PREFIX = "app"
    private const val MAX_MESSAGE_LENGTH = 4096
    private const val MAX_FILE_SIZE = 20L * 1024 * 1024       // 20 MB
    private const val MAX_HISTORY = 6
    private const val TOTAL_SIZE_CAP = MAX_FILE_SIZE * MAX_HISTORY

    val logsDirectory: String by lazy {
        val context = NitroModules.applicationContext
        if (context == null) {
            android.util.Log.e("OneKeyLog", "NitroModules.applicationContext is null, file logging disabled")
            ""
        } else {
            "${context.cacheDir.absolutePath}/logs"
        }
    }

    // Nullable: null when applicationContext is unavailable (file logging disabled)
    private val logger: Logger? by lazy {
        val dir = logsDirectory
        if (dir.isEmpty()) {
            android.util.Log.w("OneKeyLog", "File logging disabled: no valid logs directory")
            return@lazy null
        }

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
        onekeyLogger.detachAppender(APPENDER_NAME)
        onekeyLogger.addAppender(appender)
        onekeyLogger.isAdditive = false  // Do not propagate to ROOT logger

        onekeyLogger
    }

    private fun truncate(message: String): String {
        return if (message.length > MAX_MESSAGE_LENGTH) {
            message.substring(0, MAX_MESSAGE_LENGTH) + "...(truncated)"
        } else {
            message
        }
    }

    @JvmStatic
    fun debug(tag: String, message: String) { logger?.debug(truncate("[$tag] $message")) }

    @JvmStatic
    fun info(tag: String, message: String) { logger?.info(truncate("[$tag] $message")) }

    @JvmStatic
    fun warn(tag: String, message: String) { logger?.warn(truncate("[$tag] $message")) }

    @JvmStatic
    fun error(tag: String, message: String) { logger?.error(truncate("[$tag] $message")) }
}

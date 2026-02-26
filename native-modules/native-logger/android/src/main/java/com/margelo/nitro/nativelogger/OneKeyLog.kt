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
    private const val MAX_FILE_SIZE = 20L * 1024 * 1024       // 20 MB
    private const val MAX_HISTORY = 6
    private const val TOTAL_SIZE_CAP = MAX_FILE_SIZE * MAX_HISTORY

    val logsDirectory: String by lazy {
        val context = NitroModules.applicationContext
            ?: throw IllegalStateException("NitroModules.applicationContext is null")
        "${context.cacheDir.absolutePath}/logs"
    }

    private val logger: Logger by lazy {
        val dir = logsDirectory
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

        val root = LoggerFactory.getLogger(
            Logger.ROOT_LOGGER_NAME
        ) as ch.qos.logback.classic.Logger
        root.level = Level.DEBUG
        // Remove all existing appenders (including default console) to avoid duplicate logcat output
        root.detachAndStopAllAppenders()
        root.addAppender(appender)

        LoggerFactory.getLogger("OneKey")
    }

    @JvmStatic
    fun debug(tag: String, message: String) = logger.debug("[$tag] $message")

    @JvmStatic
    fun info(tag: String, message: String) = logger.info("[$tag] $message")

    @JvmStatic
    fun warn(tag: String, message: String) = logger.warn("[$tag] $message")

    @JvmStatic
    fun error(tag: String, message: String) = logger.error("[$tag] $message")
}

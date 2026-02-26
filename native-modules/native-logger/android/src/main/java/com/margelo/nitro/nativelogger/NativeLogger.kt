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
        }
    }

    override fun getLogFilePaths(): Promise<Array<String>> {
        return Promise.async {
            val dir = OneKeyLog.logsDirectory
            File(dir).listFiles { _, name -> name.endsWith(".log") }
                ?.map { it.absolutePath }?.toTypedArray() ?: arrayOf()
        }
    }

    override fun deleteLogFiles(): Promise<Unit> {
        return Promise.async {
            val dir = OneKeyLog.logsDirectory
            File(dir).listFiles { _, name -> name.endsWith(".log") }
                ?.forEach { it.delete() }
        }
    }
}

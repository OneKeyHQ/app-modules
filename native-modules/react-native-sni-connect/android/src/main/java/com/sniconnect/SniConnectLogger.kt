package com.sniconnect

/**
 * Lightweight logging wrapper that dynamically dispatches to OneKeyLog.
 * Uses reflection to avoid a hard dependency on the (nitro-based) native-logger
 * module. Falls back to android.util.Log when OneKeyLog is not available.
 *
 * Mirrors iOS SniConnectLog and the existing BTLogger / SBLLogger.
 */
internal object SniConnectLogger {
  private const val TAG = "SniConnect"
  private val bracketedIpv6Regex =
    Regex("""\[([0-9A-Fa-f:.]*:[0-9A-Fa-f:.]*)\](?=$|:\d{1,5}\b|[^0-9A-Fa-f:.\]])""")
  private val compressedIpv6Regex =
    Regex("""(^|[^0-9A-Fa-f:.\[])([0-9A-Fa-f:.]*::[0-9A-Fa-f:.]*)(?=$|[^0-9A-Fa-f:.\]])""")
  private val fullIpv6Regex =
    Regex("""(^|[^0-9A-Fa-f:.\[])([0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4}){7})(?=$|[^0-9A-Fa-f:.\]])""")
  private val ipv4Regex =
    Regex("""(^|[^\d.])((?:25[0-5]|2[0-4]\d|1?\d?\d)(?:\.(?:25[0-5]|2[0-4]\d|1?\d?\d)){3})(?=$|[^\d.])""")

  private val logClass: Class<*>? by lazy {
    try {
      Class.forName("com.margelo.nitro.nativelogger.OneKeyLog")
    } catch (_: ClassNotFoundException) {
      null
    }
  }

  private val methods by lazy {
    val cls = logClass ?: return@lazy null
    mapOf(
      "debug" to cls.getMethod("debug", String::class.java, String::class.java),
      "info" to cls.getMethod("info", String::class.java, String::class.java),
      "warn" to cls.getMethod("warn", String::class.java, String::class.java),
      "error" to cls.getMethod("error", String::class.java, String::class.java),
    )
  }

  @JvmStatic
  fun debug(message: String) = log("debug", message, android.util.Log.DEBUG)

  @JvmStatic
  fun info(message: String) = log("info", message, android.util.Log.INFO)

  @JvmStatic
  fun warn(message: String) = log("warn", message, android.util.Log.WARN)

  @JvmStatic
  fun error(message: String) = log("error", message, android.util.Log.ERROR)

  fun event(name: String, vararg fields: Pair<String, Any?>): String =
    (listOf("event" to name) + fields).joinToString(separator = " ") { (key, value) ->
      "$key=${sanitize(value)}"
    }

  fun shortHash(value: String?): String {
    if (value.isNullOrEmpty()) return "none"
    var hash = 0xcbf29ce484222325UL
    value.encodeToByteArray().forEach { byte ->
      hash = hash xor byte.toUByte().toULong()
      hash *= 0x100000001b3UL
    }
    return hash.toString(16).padStart(16, '0').take(12)
  }

  fun elapsedMs(startedAtMs: Long): Long =
    (android.os.SystemClock.elapsedRealtime() - startedAtMs).coerceAtLeast(0)

  fun ipFamily(ip: String): String = if (ip.contains(":")) "ipv6" else "ipv4"

  private fun log(level: String, message: String, androidLogLevel: Int) {
    val method = methods?.get(level)
    if (method != null) {
      try {
        method.invoke(null, TAG, message)
        return
      } catch (_: Exception) {
        // Fall through to android.util.Log
      }
    }
    try {
      android.util.Log.println(androidLogLevel, TAG, message)
    } catch (_: RuntimeException) {
      // Android JVM unit tests do not mock android.util.Log.
    }
  }

  private fun sanitize(value: Any?): String =
    value?.toString()
      ?.let(::redactIpLiterals)
      ?.replace('\n', '_')
      ?.replace('\r', '_')
      ?.replace(' ', '_')
      ?: "none"

  private fun redactIpLiterals(value: String): String =
    value
      .replace(bracketedIpv6Regex, "<ip6>")
      .replace(compressedIpv6Regex) { "${it.groupValues[1]}<ip6>" }
      .replace(fullIpv6Regex) { "${it.groupValues[1]}<ip6>" }
      .replace(ipv4Regex) { "${it.groupValues[1]}<ip>" }
}

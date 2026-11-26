package com.margelo.nitro.reactnativedeviceutils

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

internal class FirstLaunchHistory(
  directory: File,
  appVersion: String?,
  previousAppVersion: String?,
  hasPreviousLaunch: Boolean,
) {
  private data class PersistedState(
    val hasLaunched: Boolean,
    val highestAppVersion: String?,
    val highestBundleVersion: String?,
  )

  val isFirstLaunch: Boolean
  val isFirstLaunchForCurrentAppVersion: Boolean
  private val directory: File = directory
  private val stateFile = File(directory, FILE_NAME)
  private var persisted: PersistedState
  private val storageAvailable: Boolean
  private val bundleResults = mutableMapOf<String, Boolean>()
  private var initialBundleVersion: String? = null

  init {
    val validPrevious = validatedVersion(previousAppVersion)
    val validCurrent = validatedVersion(appVersion)
    val fileExists = stateFile.exists()
    val loaded = readState(stateFile)
    if (fileExists && loaded == null) {
      storageAvailable = false
      persisted = PersistedState(
        hasLaunched = true,
        highestAppVersion = validPrevious,
        highestBundleVersion = null,
      )
      isFirstLaunch = false
      isFirstLaunchForCurrentAppVersion = false
    } else {
      var state = loaded ?: PersistedState(
        hasLaunched = false,
        highestAppVersion = validPrevious,
        highestBundleVersion = null,
      )
      var needsWrite = loaded == null

      val computedFirstLaunch = !state.hasLaunched && !hasPreviousLaunch
      if (!state.hasLaunched) {
        state = state.copy(hasLaunched = true)
        needsWrite = true
      }

      val computedFirstAppVersion = if (validCurrent != null) {
        val baseline = state.highestAppVersion
        val isHigher = baseline == null || compareVersions(validCurrent, baseline) > 0
        if (isHigher) {
          state = state.copy(highestAppVersion = validCurrent)
          needsWrite = true
        }
        isHigher
      } else {
        false
      }

      if (needsWrite) {
        if (writeState(state)) {
          storageAvailable = true
          persisted = state
          isFirstLaunch = computedFirstLaunch
          isFirstLaunchForCurrentAppVersion = computedFirstAppVersion
        } else {
          storageAvailable = false
          persisted = state
          isFirstLaunch = false
          isFirstLaunchForCurrentAppVersion = false
        }
      } else {
        storageAvailable = true
        persisted = state
        isFirstLaunch = computedFirstLaunch
        isFirstLaunchForCurrentAppVersion = computedFirstAppVersion
      }
    }
  }

  @Synchronized
  fun isFirstLaunchForBundle(bundleVersion: String?): Boolean {
    if (bundleVersion == null) {
      return initialBundleVersion?.let { bundleResults[it] } ?: false
    }
    val validBundle = validatedVersion(bundleVersion) ?: return false
    if (initialBundleVersion == null) {
      initialBundleVersion = validBundle
    }
    bundleResults[validBundle]?.let {
      return it
    }
    if (!storageAvailable) {
      bundleResults[validBundle] = false
      return false
    }
    val baseline = persisted.highestBundleVersion
    val isHigher = baseline == null || compareVersions(validBundle, baseline) > 0
    if (isHigher) {
      val next = persisted.copy(highestBundleVersion = validBundle)
      if (!writeState(next)) {
        bundleResults[validBundle] = false
        return false
      }
      persisted = next
    }
    bundleResults[validBundle] = isHigher
    return isHigher
  }

  private fun readState(file: File): PersistedState? = try {
    if (!file.isFile) {
      null
    } else {
      parsePersistedJson(file.readText(Charsets.UTF_8))
    }
  } catch (_: IOException) {
    null
  } catch (_: SecurityException) {
    null
  }

  private fun writeState(state: PersistedState): Boolean {
    var temp: File? = null
    return try {
      if (!directory.exists() && !directory.mkdirs() && !directory.isDirectory) {
        false
      } else if (!directory.isDirectory) {
        false
      } else {
        val appJson = state.highestAppVersion?.let { "\"$it\"" } ?: "null"
        val bundleJson = state.highestBundleVersion?.let { "\"$it\"" } ?: "null"
        val json =
          """{"hasLaunched":${state.hasLaunched},"highestAppVersion":$appJson,"highestBundleVersion":$bundleJson}"""
        temp = File.createTempFile("${stateFile.name}.", ".tmp", directory)
        FileOutputStream(temp).use { fos ->
          fos.write(json.toByteArray(Charsets.UTF_8))
          fos.fd.sync()
        }
        if (temp.renameTo(stateFile)) {
          temp = null
          true
        } else {
          false
        }
      }
    } catch (_: IOException) {
      false
    } catch (_: SecurityException) {
      false
    } finally {
      temp?.delete()
    }
  }

  private fun parsePersistedJson(raw: String): PersistedState? {
    var pos = 0
    val len = raw.length

    fun skipWs() {
      while (pos < len && raw[pos] in " \t\r\n") pos++
    }

    fun readString(): String? {
      if (pos >= len || raw[pos] != '"') return null
      pos++
      val sb = StringBuilder()
      while (pos < len) {
        val c = raw[pos++]
        when {
          c == '"' -> return sb.toString()
          c == '\\' -> {
            if (pos >= len) return null
            val esc = raw[pos++]
            when (esc) {
              '"', '\\', '/' -> sb.append(esc)
              'b' -> sb.append('\b')
              'f' -> sb.append('\u000C')
              'n' -> sb.append('\n')
              'r' -> sb.append('\r')
              't' -> sb.append('\t')
              'u' -> {
                if (pos + 4 > len) return null
                val hex = raw.substring(pos, pos + 4)
                val code = hex.toIntOrNull(16) ?: return null
                sb.append(code.toChar())
                pos += 4
              }
              else -> return null
            }
          }
          c.code < 0x20 -> return null
          else -> sb.append(c)
        }
      }
      return null
    }

    fun readLiteral(expected: String): Boolean {
      if (raw.regionMatches(pos, expected, 0, expected.length)) {
        pos += expected.length
        return true
      }
      return false
    }

    skipWs()
    if (pos >= len || raw[pos] != '{') return null
    pos++
    skipWs()

    var hasLaunched: Boolean? = null
    var highestAppVersion: String? = null
    var highestBundleVersion: String? = null
    var sawAppKey = false
    var sawBundleKey = false

    if (pos < len && raw[pos] == '}') {
      // Missing required hasLaunched field.
      return null
    }

    while (true) {
      skipWs()
      val key = readString() ?: return null
      skipWs()
      if (pos >= len || raw[pos] != ':') return null
      pos++
      skipWs()
      when (key) {
        "hasLaunched" -> {
          if (hasLaunched != null) return null
          hasLaunched = when {
            readLiteral("true") -> true
            readLiteral("false") -> false
            else -> return null
          }
        }
        "highestAppVersion" -> {
          if (sawAppKey) return null
          sawAppKey = true
          highestAppVersion = if (readLiteral("null")) {
            null
          } else {
            val s = readString() ?: return null
            validatedVersion(s) ?: return null
          }
        }
        "highestBundleVersion" -> {
          if (sawBundleKey) return null
          sawBundleKey = true
          highestBundleVersion = if (readLiteral("null")) {
            null
          } else {
            val s = readString() ?: return null
            validatedVersion(s) ?: return null
          }
        }
        else -> return null
      }
      skipWs()
      if (pos >= len) return null
      when (raw[pos]) {
        ',' -> pos++
        '}' -> {
          pos++
          break
        }
        else -> return null
      }
    }

    skipWs()
    if (pos != len) return null
    val resolvedHasLaunched = hasLaunched ?: return null
    return PersistedState(
      hasLaunched = resolvedHasLaunched,
      highestAppVersion = highestAppVersion,
      highestBundleVersion = highestBundleVersion,
    )
  }

  companion object {
    internal const val FILE_NAME = "onekey-device-utils-first-launch.json"

    private val VERSION_REGEX =
      Regex("""^[0-9]+(?:\.[0-9]+)*(?:-[0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*)?(?:\+[0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*)?$""")

    internal fun validatedVersion(version: String?): String? {
      if (version == null || version.length !in 1..120) return null
      if (!VERSION_REGEX.matches(version)) return null
      return version
    }

    internal fun compareVersions(a: String, b: String): Int {
      fun stripBuild(v: String): String {
        val idx = v.indexOf('+')
        return if (idx >= 0) v.substring(0, idx) else v
      }
      fun splitPre(v: String): Pair<String, String?> {
        val idx = v.indexOf('-')
        return if (idx >= 0) v.substring(0, idx) to v.substring(idx + 1) else v to null
      }
      fun isDigits(s: String): Boolean = s.isNotEmpty() && s.all { it in '0'..'9' }
      fun compareUnsigned(lhs: String, rhs: String): Int {
        val normA = lhs.trimStart('0').ifEmpty { "0" }
        val normB = rhs.trimStart('0').ifEmpty { "0" }
        if (normA.length != normB.length) return if (normA.length < normB.length) -1 else 1
        return normA.compareTo(normB)
      }
      fun compareIdent(lhs: String, rhs: String): Int {
        val aNum = isDigits(lhs)
        val bNum = isDigits(rhs)
        if (aNum && bNum) return compareUnsigned(lhs, rhs)
        if (aNum != bNum) return if (aNum) -1 else 1
        return lhs.compareTo(rhs)
      }

      val (mainA, preA) = splitPre(stripBuild(a))
      val (mainB, preB) = splitPre(stripBuild(b))
      val segA = mainA.split('.')
      val segB = mainB.split('.')
      val maxLen = maxOf(segA.size, segB.size)

      for (i in 0 until maxLen) {
        val partA = if (i < segA.size) segA[i] else "0"
        val partB = if (i < segB.size) segB[i] else "0"
        val cmp = compareIdent(partA, partB)
        if (cmp != 0) return cmp
      }

      if (preA == null && preB == null) return 0
      if (preA == null) return 1
      if (preB == null) return -1

      val preSegA = preA.split('.')
      val preSegB = preB.split('.')
      val maxPre = maxOf(preSegA.size, preSegB.size)
      for (i in 0 until maxPre) {
        if (i >= preSegA.size) return -1
        if (i >= preSegB.size) return 1
        val cmp = compareIdent(preSegA[i], preSegB[i])
        if (cmp != 0) return cmp
      }
      return 0
    }
  }
}

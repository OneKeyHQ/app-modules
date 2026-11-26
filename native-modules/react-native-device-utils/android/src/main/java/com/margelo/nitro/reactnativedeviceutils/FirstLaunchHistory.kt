package com.margelo.nitro.reactnativedeviceutils

import java.io.File
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
  private var lastBundleVersion: String? = null

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
      return lastBundleVersion?.let { bundleResults[it] } ?: false
    }
    val validBundle = validatedVersion(bundleVersion) ?: return false
    bundleResults[validBundle]?.let {
      lastBundleVersion = validBundle
      return it
    }
    if (!storageAvailable) {
      bundleResults[validBundle] = false
      lastBundleVersion = validBundle
      return false
    }
    val baseline = persisted.highestBundleVersion
    val isHigher = baseline == null || compareVersions(validBundle, baseline) > 0
    if (isHigher) {
      val next = persisted.copy(highestBundleVersion = validBundle)
      if (!writeState(next)) {
        bundleResults[validBundle] = false
        lastBundleVersion = validBundle
        return false
      }
      persisted = next
    }
    bundleResults[validBundle] = isHigher
    lastBundleVersion = validBundle
    return isHigher
  }

  private fun readState(file: File): PersistedState? = try {
    if (!file.isFile) {
      null
    } else {
      val text = file.readText(Charsets.UTF_8)
      val hasLaunched = Regex(""""hasLaunched"\s*:\s*(true|false)""")
        .find(text)?.groupValues?.get(1) == "true"
      val highestApp = Regex(""""highestAppVersion"\s*:\s*"([^"]*)"""")
        .find(text)?.groupValues?.get(1)?.let(::validatedVersion)
      val highestBundle = Regex(""""highestBundleVersion"\s*:\s*"([^"]*)"""")
        .find(text)?.groupValues?.get(1)?.let(::validatedVersion)
      PersistedState(
        hasLaunched = hasLaunched,
        highestAppVersion = highestApp,
        highestBundleVersion = highestBundle,
      )
    }
  } catch (_: IOException) {
    null
  } catch (_: SecurityException) {
    null
  }

  private fun writeState(state: PersistedState): Boolean = try {
    if (!directory.exists() && !directory.mkdirs() && !directory.isDirectory) {
      false
    } else if (!directory.isDirectory) {
      false
    } else {
      val appJson = state.highestAppVersion?.let { "\"$it\"" } ?: "null"
      val bundleJson = state.highestBundleVersion?.let { "\"$it\"" } ?: "null"
      val json =
        """{"hasLaunched":${state.hasLaunched},"highestAppVersion":$appJson,"highestBundleVersion":$bundleJson}"""
      val temp = File(directory, "${stateFile.name}.tmp")
      temp.writeText(json, Charsets.UTF_8)
      if (!temp.renameTo(stateFile)) {
        stateFile.writeText(json, Charsets.UTF_8)
        temp.delete()
      }
      true
    }
  } catch (_: IOException) {
    false
  } catch (_: SecurityException) {
    false
  }

  companion object {
    internal const val FILE_NAME = "onekey-device-utils-first-launch.json"

    internal fun validatedVersion(version: String?): String? {
      if (version == null || version.length !in 1..120) return null
      if (!version.all { it in '0'..'9' || it in 'A'..'Z' || it in 'a'..'z' || it in "._-+" }) return null
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

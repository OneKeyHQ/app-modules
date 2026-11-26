package com.margelo.nitro.reactnativedeviceutils

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

internal class FirstLaunchHistory(
  baseDirectory: File,
  appVersion: String?,
  previousAppVersion: String?,
  hasPreviousLaunch: Boolean,
) {
  val isFirstLaunch: Boolean
  val isFirstLaunchForCurrentAppVersion: Boolean
  private val storeDirectory = File(baseDirectory, DIRECTORY_NAME)
  private val bundleResults = mutableMapOf<String, Boolean>()
  private var initialBundleVersion: String? = null

  init {
    val launchedFile = File(storeDirectory, LAUNCHED_FILE_NAME)
    val launchedExists = launchedFile.exists()
    isFirstLaunch = if (launchedExists) {
      false
    } else {
      val wrote = writeString(launchedFile, "1")
      wrote && !hasPreviousLaunch
    }

    val legacyBaseline = if (launchedExists) null else validatedVersion(previousAppVersion)
    val validCurrent = validatedVersion(appVersion)
    isFirstLaunchForCurrentAppVersion = if (validCurrent != null) {
      evaluateVersionFile(
        file = File(storeDirectory, APP_VERSION_FILE_NAME),
        currentVersion = validCurrent,
        legacyBaseline = legacyBaseline,
      )
    } else {
      false
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
    val result = evaluateVersionFile(
      file = File(storeDirectory, BUNDLE_VERSION_FILE_NAME),
      currentVersion = validBundle,
      legacyBaseline = null,
    )
    bundleResults[validBundle] = result
    return result
  }

  private fun evaluateVersionFile(
    file: File,
    currentVersion: String,
    legacyBaseline: String?,
  ): Boolean {
    val existing: String? = if (file.exists()) {
      val raw = try {
        if (!file.isFile) return false
        file.readText(Charsets.UTF_8)
      } catch (_: IOException) {
        return false
      } catch (_: SecurityException) {
        return false
      }
      validatedVersion(raw) ?: return false
    } else {
      null
    }

    val baseline = existing ?: legacyBaseline
    val isHigher = baseline == null || compareVersions(currentVersion, baseline) > 0
    if (isHigher) {
      return writeString(file, currentVersion)
    }
    if (existing == null) {
      writeString(file, baseline)
    }
    return false
  }

  private fun writeString(target: File, value: String): Boolean {
    var temp: File? = null
    return try {
      if (!storeDirectory.exists() && !storeDirectory.mkdirs() && !storeDirectory.isDirectory) {
        false
      } else if (!storeDirectory.isDirectory) {
        false
      } else {
        temp = File.createTempFile("${target.name}.", ".tmp", storeDirectory)
        FileOutputStream(temp).use { fos ->
          fos.write(value.toByteArray(Charsets.UTF_8))
          fos.fd.sync()
        }
        if (temp.renameTo(target)) {
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

  companion object {
    internal const val DIRECTORY_NAME = "1k_first_launch"
    internal const val LAUNCHED_FILE_NAME = "launched"
    internal const val APP_VERSION_FILE_NAME = "app_ver"
    internal const val BUNDLE_VERSION_FILE_NAME = "bundle_ver"

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

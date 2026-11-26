package com.margelo.nitro.reactnativedeviceutils

import android.content.Context
import android.content.pm.PackageManager
import java.io.File

/**
 * Pure native process-wide singleton for first-launch tracking.
 * Has no Nitro/JSI dependencies, so host apps can initialize it early in
 * `Application.onCreate()` before React Native starts.
 */
object DeviceUtilsFirstLaunch {
  private val lock = Any()

  @Volatile
  private var history: FirstLaunchHistory? = null

  /**
   * Returns true if the persisted first-launch state file already exists on disk.
   */
  @JvmStatic
  fun hasPersistedState(context: Context): Boolean {
    val filesDir = context.applicationContext?.filesDir ?: context.filesDir ?: return false
    return File(filesDir, FirstLaunchHistory.FILE_NAME).exists()
  }

  /**
   * Initializes the process-wide first-launch state. Subsequent calls are no-ops.
   */
  @JvmStatic
  @JvmOverloads
  fun initialize(
    context: Context,
    isLegacyInstall: Boolean = false,
    legacyAppVersion: String? = null,
  ) {
    initialize(
      context = context,
      isLegacyInstall = { isLegacyInstall },
      legacyAppVersion = { legacyAppVersion },
    )
  }

  /**
   * Kotlin-friendly initializer whose legacy-detection lambdas are only evaluated
   * when no persisted state file exists yet.
   */
  fun initialize(
    context: Context,
    isLegacyInstall: () -> Boolean,
    legacyAppVersion: () -> String? = { null },
  ) {
    if (history != null) return
    synchronized(lock) {
      if (history != null) return
      val appContext = context.applicationContext ?: context
      val filesDirectory = appContext.filesDir ?: return
      val versionName = try {
        @Suppress("DEPRECATION")
        appContext.packageManager.getPackageInfo(appContext.packageName, 0).versionName
      } catch (_: PackageManager.NameNotFoundException) {
        null
      } catch (_: SecurityException) {
        null
      }
      val persistedExists = File(filesDirectory, FirstLaunchHistory.FILE_NAME).exists()
      val hasPreviousLaunch = if (persistedExists) false else runCatching(isLegacyInstall).getOrDefault(false)
      val previousVersion = if (persistedExists) null else runCatching(legacyAppVersion).getOrNull()
      history = FirstLaunchHistory(
        directory = filesDirectory,
        appVersion = versionName,
        previousAppVersion = previousVersion,
        hasPreviousLaunch = hasPreviousLaunch,
      )
    }
  }

  internal fun isFirstLaunch(fallbackContext: Context? = null): Boolean {
    ensureInitialized(fallbackContext)
    return history?.isFirstLaunch ?: false
  }

  internal fun isFirstLaunchForCurrentAppVersion(fallbackContext: Context? = null): Boolean {
    ensureInitialized(fallbackContext)
    return history?.isFirstLaunchForCurrentAppVersion ?: false
  }

  internal fun isFirstLaunchForBundle(
    bundleVersion: String?,
    fallbackContext: Context? = null,
  ): Boolean {
    ensureInitialized(fallbackContext)
    return history?.isFirstLaunchForBundle(bundleVersion) ?: false
  }

  private fun ensureInitialized(fallbackContext: Context?) {
    if (history == null && fallbackContext != null) {
      initialize(fallbackContext)
    }
  }
}

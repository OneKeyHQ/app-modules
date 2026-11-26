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

  private fun baseDirectory(context: Context): File? {
    val appContext = context.applicationContext ?: context
    return appContext.noBackupFilesDir
  }

  /**
   * Returns true if the persisted launched marker already exists on disk.
   */
  @JvmStatic
  fun hasPersistedState(context: Context): Boolean {
    val base = baseDirectory(context) ?: return false
    return File(File(base, FirstLaunchHistory.DIRECTORY_NAME), FirstLaunchHistory.LAUNCHED_FILE_NAME).exists()
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
   * when no persisted launched marker exists yet.
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
      val base = baseDirectory(appContext) ?: return
      val versionName = try {
        @Suppress("DEPRECATION")
        appContext.packageManager.getPackageInfo(appContext.packageName, 0).versionName
      } catch (_: PackageManager.NameNotFoundException) {
        null
      } catch (_: SecurityException) {
        null
      }
      val persistedExists =
        File(File(base, FirstLaunchHistory.DIRECTORY_NAME), FirstLaunchHistory.LAUNCHED_FILE_NAME).exists()
      val hasPreviousLaunch = if (persistedExists) false else runCatching(isLegacyInstall).getOrDefault(false)
      val previousVersion = if (persistedExists) null else runCatching(legacyAppVersion).getOrNull()
      history = FirstLaunchHistory(
        baseDirectory = base,
        appVersion = versionName,
        previousAppVersion = previousVersion,
        hasPreviousLaunch = hasPreviousLaunch,
      )
    }
  }

  /**
   * Deletes persisted first-launch files and clears in-memory state so a subsequent
   * JS restart within the same native process observes a fresh launch.
   * Returns true if persisted files were removed (or already absent), false on failure.
   */
  @JvmStatic
  fun reset(context: Context): Boolean {
    synchronized(lock) {
      val base = baseDirectory(context) ?: return false
      val storeDir = File(base, FirstLaunchHistory.DIRECTORY_NAME)
      if (storeDir.exists()) {
        val deleted = try {
          storeDir.deleteRecursively() && !storeDir.exists()
        } catch (_: SecurityException) {
          false
        }
        if (!deleted) {
          return false
        }
      }
      history = null
      return true
    }
  }

  internal fun isFirstLaunch(fallbackContext: Context? = null): Boolean = synchronized(lock) {
    ensureInitializedLocked(fallbackContext)
    history?.isFirstLaunch ?: false
  }

  internal fun isFirstLaunchForCurrentAppVersion(fallbackContext: Context? = null): Boolean = synchronized(lock) {
    ensureInitializedLocked(fallbackContext)
    history?.isFirstLaunchForCurrentAppVersion ?: false
  }

  internal fun isFirstLaunchForBundle(
    bundleVersion: String?,
    fallbackContext: Context? = null,
  ): Boolean = synchronized(lock) {
    ensureInitializedLocked(fallbackContext)
    history?.isFirstLaunchForBundle(bundleVersion) ?: false
  }

  private fun ensureInitializedLocked(fallbackContext: Context?) {
    if (history == null && fallbackContext != null) {
      initialize(fallbackContext)
    }
  }
}

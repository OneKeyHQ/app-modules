package com.margelo.nitro.qrscanner

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MainThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.facebook.react.modules.core.PermissionAwareActivity
import java.lang.ref.WeakReference
import java.util.UUID
import java.util.WeakHashMap

/** Shares one camera permission request per host activity. */
@MainThread
internal object QrCameraPermission {
  enum class Result { GRANTED, DENIED, CANCELLED }

  private const val FIRST_REQUEST_CODE = 0x5152
  private const val LAST_REQUEST_CODE = 0x51ff
  private const val FOREGROUND_TIMEOUT_MS = 60_000L
  private const val LEGACY_MAX_WAIT_MS = 300_000L
  private const val MAX_RETRY_WAIT_MS = 300_000L
  private const val RETRY_INTERVAL_MS = 1_000L
  private const val LEGACY_CHECK_MS = 1_000L
  private val main = Handler(Looper.getMainLooper())
  private var nextRequestCode = FIRST_REQUEST_CODE

  private class Registration(val hasWindowFocus: () -> Boolean, val listener: (Result) -> Unit)

  private class Request {
    val listeners = LinkedHashSet<Registration>()
    var cleanup: (() -> Unit)? = null
    var retryStartedAt: Long? = null
  }

  private val pending = WeakHashMap<PermissionAwareActivity, Request>()

  fun request(
    activity: PermissionAwareActivity,
    hasWindowFocus: () -> Boolean = { false },
    listener: (Result) -> Unit,
  ): () -> Unit {
    val registration = Registration(hasWindowFocus, listener)
    val previous = pending[activity]
    val request = previous ?: Request().also { pending[activity] = it }
    request.listeners.add(registration)
    if (previous == null) start(activity, request)
    return { request.listeners.remove(registration) }
  }

  private fun start(activity: PermissionAwareActivity, request: Request) {
    val activityRef = WeakReference(activity)
    var settled = false
    fun finish(result: Result?) {
      val owner = activityRef.get() ?: return
      if (pending[owner] !== request) return
      pending.remove(owner)
      request.cleanup?.invoke()
      request.cleanup = null
      val listeners = request.listeners.toList()
      request.listeners.clear()
      if (result != null) listeners.forEach { it.listener(result) }
    }

    fun onResult(result: Result?) {
      val owner = activityRef.get() ?: return
      if (settled || pending[owner] !== request) return
      settled = true
      if (result != Result.CANCELLED || request.listeners.isEmpty()) {
        finish(result)
        return
      }

      // Android returns an empty result when it cannot start a second prompt.
      // Keep the shared waiters and retry once the host can present it again.
      request.cleanup?.invoke()
      val startedAt = request.retryStartedAt ?: SystemClock.uptimeMillis().also {
        request.retryStartedAt = it
      }
      val retry = object : Runnable {
        override fun run() {
          val host = activityRef.get() ?: return
          val lifecycle = (host as? LifecycleOwner)?.lifecycle?.currentState
          when {
            request.listeners.isEmpty() || isDestroyed(host, lifecycle) -> finish(null)
            hasCameraPermission(host) -> finish(Result.GRANTED)
            SystemClock.uptimeMillis() - startedAt >= MAX_RETRY_WAIT_MS -> finish(Result.CANCELLED)
            isResumed(lifecycle) &&
              (hasHostWindowFocus(host) || request.listeners.any { it.hasWindowFocus() }) -> {
              request.cleanup = null
              start(host, request)
            }
            else -> main.postDelayed(this, RETRY_INTERVAL_MS)
          }
        }
      }
      request.cleanup = { main.removeCallbacks(retry) }
      main.postDelayed(retry, RETRY_INTERVAL_MS)
    }

    try {
      // ReactActivity's super.onRequestPermissionsResult dispatches here.
      // Other modules can replace its delegate listener without losing ours.
      if (activity is ActivityResultRegistryOwner && activity is LifecycleOwner) {
        val lifecycle = activity.lifecycle
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
          finish(Result.DENIED)
          return
        }
        val observer = object : DefaultLifecycleObserver {
          override fun onDestroy(owner: LifecycleOwner) { finish(null) }
        }
        val launcher = activity.activityResultRegistry.register(
          "onekey-qr-camera-${UUID.randomUUID()}", ActivityResultContracts.RequestMultiplePermissions(),
        ) { grants ->
          val result = when (grants[Manifest.permission.CAMERA]) {
            true -> Result.GRANTED
            false -> Result.DENIED
            null -> Result.CANCELLED
          }
          // Let the registry finish dispatch before cleanup unregisters the key.
          main.post { onResult(result) }
        }
        request.cleanup = {
          launcher.unregister()
          lifecycle.removeObserver(observer)
        }
        lifecycle.addObserver(observer)
        launcher.launch(arrayOf(Manifest.permission.CAMERA))
      } else {
        startLegacy(activity, activityRef, request, ::onResult)
      }
    } catch (_: RuntimeException) {
      finish(Result.DENIED)
    }
  }

  private fun startLegacy(
    activity: PermissionAwareActivity,
    activityRef: WeakReference<PermissionAwareActivity>,
    request: Request,
    onResult: (Result?) -> Unit,
  ) {
    val requestCode = nextRequestCode
    nextRequestCode = if (requestCode == LAST_REQUEST_CODE) FIRST_REQUEST_CODE else requestCode + 1
    val startedAt = SystemClock.uptimeMillis()
    val interval = if (activity is Activity || activity is LifecycleOwner) LEGACY_CHECK_MS else FOREGROUND_TIMEOUT_MS
    var foregroundSince: Long? = if (interval == FOREGROUND_TIMEOUT_MS) startedAt else null
    lateinit var expiry: Runnable
    expiry = Runnable {
      val owner = activityRef.get() ?: return@Runnable
      val lifecycle = (owner as? LifecycleOwner)?.lifecycle?.currentState
      if (isDestroyed(owner, lifecycle)) {
        onResult(null)
        return@Runnable
      }
      val now = SystemClock.uptimeMillis()
      val inForeground = isResumed(lifecycle) && hasHostWindowFocus(owner)
      foregroundSince = if (inForeground) foregroundSince ?: now else null
      val elapsedInForeground = foregroundSince?.let { now - it } ?: 0
      if (elapsedInForeground >= FOREGROUND_TIMEOUT_MS || now - startedAt >= LEGACY_MAX_WAIT_MS) {
        onResult(if (hasCameraPermission(owner)) Result.GRANTED else Result.DENIED)
      } else {
        main.postDelayed(expiry, interval)
      }
    }
    request.cleanup = { main.removeCallbacks(expiry) }
    main.postDelayed(expiry, interval)
    activity.requestPermissions(arrayOf(Manifest.permission.CAMERA), requestCode) { code, _, results ->
      if (code != requestCode) return@requestPermissions false
      onResult(when (results.firstOrNull()) {
        PackageManager.PERMISSION_GRANTED -> Result.GRANTED
        null -> Result.CANCELLED
        else -> Result.DENIED
      })
      true
    }
  }

  private fun hasCameraPermission(host: PermissionAwareActivity): Boolean =
    host.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

  private fun isDestroyed(host: PermissionAwareActivity, lifecycle: Lifecycle.State?): Boolean =
    lifecycle == Lifecycle.State.DESTROYED || (host is Activity && host.isDestroyed)

  private fun isResumed(lifecycle: Lifecycle.State?): Boolean =
    lifecycle == null || lifecycle.isAtLeast(Lifecycle.State.RESUMED)

  private fun hasHostWindowFocus(host: PermissionAwareActivity): Boolean =
    host !is Activity || host.hasWindowFocus()
}

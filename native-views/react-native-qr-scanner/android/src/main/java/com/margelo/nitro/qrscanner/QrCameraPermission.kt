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
  private const val FIRST_REQUEST_CODE = 0x5152
  private const val LAST_REQUEST_CODE = 0x51ff
  private const val FOREGROUND_TIMEOUT_MS = 60_000L
  private const val LEGACY_MAX_WAIT_MS = 300_000L
  private const val LEGACY_CHECK_MS = 1_000L
  private val main = Handler(Looper.getMainLooper())
  private var nextRequestCode = FIRST_REQUEST_CODE

  private class Request {
    val listeners = LinkedHashSet<(Boolean) -> Unit>()
    var cleanup: (() -> Unit)? = null
  }

  private val pending = WeakHashMap<PermissionAwareActivity, Request>()

  fun request(activity: PermissionAwareActivity, listener: (Boolean) -> Unit): () -> Unit {
    val registration: (Boolean) -> Unit = { granted -> listener(granted) }
    val previous = pending[activity]
    val request = previous ?: Request().also { pending[activity] = it }
    request.listeners.add(registration)
    if (previous == null) start(activity, request)
    return { request.listeners.remove(registration) }
  }

  private fun start(activity: PermissionAwareActivity, request: Request) {
    val activityRef = WeakReference(activity)
    fun finish(granted: Boolean?) {
      val owner = activityRef.get() ?: return
      if (pending[owner] !== request) return
      pending.remove(owner)
      request.cleanup?.invoke()
      request.cleanup = null
      val listeners = request.listeners.toList()
      request.listeners.clear()
      if (granted != null) listeners.forEach { it(granted) }
    }

    try {
      // ReactActivity's super.onRequestPermissionsResult dispatches here.
      // Other modules can replace its delegate listener without losing ours.
      if (activity is ActivityResultRegistryOwner && activity is LifecycleOwner) {
        val lifecycle = activity.lifecycle
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
          finish(false)
          return
        }
        val observer = object : DefaultLifecycleObserver {
          override fun onDestroy(owner: LifecycleOwner) { finish(null) }
        }
        val launcher = activity.activityResultRegistry.register(
          "onekey-qr-camera-${UUID.randomUUID()}", ActivityResultContracts.RequestPermission(),
        ) { granted -> finish(granted) }
        request.cleanup = {
          launcher.unregister()
          lifecycle.removeObserver(observer)
        }
        lifecycle.addObserver(observer)
        launcher.launch(Manifest.permission.CAMERA)
      } else {
        startLegacy(activity, activityRef, request, ::finish)
      }
    } catch (_: RuntimeException) {
      finish(false)
    }
  }

  private fun startLegacy(
    activity: PermissionAwareActivity,
    activityRef: WeakReference<PermissionAwareActivity>,
    request: Request,
    finish: (Boolean?) -> Unit,
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
      if (lifecycle == Lifecycle.State.DESTROYED || (owner is Activity && owner.isDestroyed)) {
        finish(null)
        return@Runnable
      }
      val now = SystemClock.uptimeMillis()
      val inForeground = lifecycle?.isAtLeast(Lifecycle.State.RESUMED) ?: (owner !is Activity || owner.hasWindowFocus())
      foregroundSince = if (inForeground) foregroundSince ?: now else null
      val elapsedInForeground = foregroundSince?.let { now - it } ?: 0
      if (elapsedInForeground >= FOREGROUND_TIMEOUT_MS || now - startedAt >= LEGACY_MAX_WAIT_MS) {
        finish(owner.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
      } else {
        main.postDelayed(expiry, interval)
      }
    }
    request.cleanup = { main.removeCallbacks(expiry) }
    main.postDelayed(expiry, interval)
    activity.requestPermissions(arrayOf(Manifest.permission.CAMERA), requestCode) { code, _, results ->
      if (code != requestCode) return@requestPermissions false
      finish(results.firstOrNull() == PackageManager.PERMISSION_GRANTED)
      true
    }
  }
}

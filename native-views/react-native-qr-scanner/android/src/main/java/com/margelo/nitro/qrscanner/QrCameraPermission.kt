package com.margelo.nitro.qrscanner

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.annotation.MainThread
import com.facebook.react.modules.core.PermissionAwareActivity
import java.lang.ref.WeakReference
import java.util.WeakHashMap

/** ReactActivityDelegate can retain only one permission listener per activity. */
@MainThread
internal object QrCameraPermission {
  private const val FIRST_REQUEST_CODE = 0x5152
  private const val LAST_REQUEST_CODE = 0x51ff
  private const val REQUEST_TIMEOUT_MS = 60_000L
  private const val FOCUS_CHECK_MS = 1_000L
  private val main = Handler(Looper.getMainLooper())
  private var nextRequestCode = FIRST_REQUEST_CODE
  private class Request {
    val listeners = LinkedHashSet<(Boolean) -> Unit>()
  }

  private val pending = WeakHashMap<PermissionAwareActivity, Request>()

  fun request(activity: PermissionAwareActivity, listener: (Boolean) -> Unit): () -> Unit {
    val registration: (Boolean) -> Unit = { granted -> listener(granted) }
    val previous = pending[activity]
    val request = previous ?: Request().also { pending[activity] = it }
    request.listeners.add(registration)
    if (previous == null) {
      val activityRef = WeakReference(activity)
      val requestCode = nextRequestCode
      nextRequestCode = if (requestCode == LAST_REQUEST_CODE) FIRST_REQUEST_CODE else requestCode + 1
      lateinit var expiry: Runnable
      fun finish(granted: Boolean) {
        val owner = activityRef.get() ?: return
        if (pending[owner] !== request) return
        pending.remove(owner)
        main.removeCallbacks(expiry)
        val listeners = request.listeners.toList()
        request.listeners.clear()
        listeners.forEach { it(granted) }
      }

      val interval = if (activity is Activity) FOCUS_CHECK_MS else REQUEST_TIMEOUT_MS
      var focusedSince: Long? = if (activity is Activity) null else SystemClock.uptimeMillis()
      expiry = Runnable {
        val owner = activityRef.get() ?: return@Runnable
        val now = SystemClock.uptimeMillis()
        // A permission dialog owns window focus until the user answers it.
        // Only expire a callback after the host has control again.
        if (owner is Activity && !owner.hasWindowFocus()) {
          focusedSince = null
          main.postDelayed(expiry, interval)
          return@Runnable
        }
        val since = focusedSince ?: now.also { focusedSince = it }
        val remaining = REQUEST_TIMEOUT_MS - (now - since)
        if (remaining > 0) {
          main.postDelayed(expiry, minOf(interval, remaining))
          return@Runnable
        }
        finish(owner.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
      }
      main.postDelayed(expiry, interval)
      try {
        activity.requestPermissions(arrayOf(Manifest.permission.CAMERA), requestCode) { code, _, results ->
          if (code != requestCode) return@requestPermissions false
          finish(results.firstOrNull() == PackageManager.PERMISSION_GRANTED)
          true
        }
      } catch (_: RuntimeException) {
        finish(false)
      }
    }

    return { request.listeners.remove(registration) }
  }
}

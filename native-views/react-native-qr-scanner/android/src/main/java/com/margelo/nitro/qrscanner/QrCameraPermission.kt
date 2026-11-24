package com.margelo.nitro.qrscanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
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

      expiry = Runnable {
        val owner = activityRef.get() ?: return@Runnable
        // React Native may have replaced the callback after Android processed
        // the prompt. Re-read the grant before settling its waiting views.
        finish(owner.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
      }
      main.postDelayed(expiry, REQUEST_TIMEOUT_MS)
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

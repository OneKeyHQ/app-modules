package com.margelo.nitro.qrscanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.annotation.MainThread
import com.facebook.react.modules.core.PermissionAwareActivity
import java.util.WeakHashMap

/** ReactActivityDelegate can retain only one permission listener per activity. */
@MainThread
internal object QrCameraPermission {
  private const val REQUEST_CODE = 0x5152
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
      fun finish(granted: Boolean) {
        if (pending[activity] !== request) return
        pending.remove(activity)
        val listeners = request.listeners.toList()
        request.listeners.clear()
        listeners.forEach { it(granted) }
      }

      try {
        activity.requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CODE) { code, _, results ->
          if (code != REQUEST_CODE) return@requestPermissions false
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

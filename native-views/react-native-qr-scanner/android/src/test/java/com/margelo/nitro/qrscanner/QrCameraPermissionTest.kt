package com.margelo.nitro.qrscanner

import android.content.pm.PackageManager
import com.facebook.react.modules.core.PermissionAwareActivity
import com.facebook.react.modules.core.PermissionListener
import org.junit.Assert.assertEquals
import org.junit.Test

class QrCameraPermissionTest {
  private class Activity : PermissionAwareActivity {
    var prompts = 0
    private var reply: PermissionListener? = null
    private var code = 0
    override fun checkPermission(permission: String, pid: Int, uid: Int) = PackageManager.PERMISSION_DENIED
    override fun checkSelfPermission(permission: String) = PackageManager.PERMISSION_DENIED
    override fun shouldShowRequestPermissionRationale(permission: String) = false

    override fun requestPermissions(permissions: Array<String>, requestCode: Int, listener: PermissionListener?) {
      prompts++
      reply = listener
      code = requestCode
    }

    fun answer(granted: Boolean) {
      val result = if (granted) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED
      reply?.onRequestPermissionsResult(code, emptyArray(), intArrayOf(result))
    }
  }

  @Test fun sharesThePromptAndFansOutToActiveViews() {
    val activity = Activity()
    val results = mutableListOf<Pair<Int, Boolean>>()
    val cancel = QrCameraPermission.request(activity) { results += 1 to it }
    QrCameraPermission.request(activity) { results += 2 to it }
    QrCameraPermission.request(activity) { results += 3 to it }
    cancel()
    assertEquals(1, activity.prompts)
    activity.answer(true)
    assertEquals(listOf(2 to true, 3 to true), results)

    QrCameraPermission.request(activity) { results += 4 to it }
    assertEquals(2, activity.prompts)
    activity.answer(false)
    assertEquals(4 to false, results.last())
  }
}

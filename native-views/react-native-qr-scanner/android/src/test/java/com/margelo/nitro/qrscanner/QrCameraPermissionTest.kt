package com.margelo.nitro.qrscanner

import android.app.Activity as AndroidActivity
import android.content.pm.PackageManager
import android.os.Looper
import com.facebook.react.modules.core.PermissionAwareActivity
import com.facebook.react.modules.core.PermissionListener
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class QrCameraPermissionTest {
  private class Activity : PermissionAwareActivity {
    var prompts = 0
    private var reply: PermissionListener? = null
    var code = 0
      private set
    var currentGrant = PackageManager.PERMISSION_DENIED
    override fun checkPermission(permission: String, pid: Int, uid: Int) = PackageManager.PERMISSION_DENIED
    override fun checkSelfPermission(permission: String) = currentGrant
    override fun shouldShowRequestPermissionRationale(permission: String) = false

    override fun requestPermissions(permissions: Array<String>, requestCode: Int, listener: PermissionListener?) {
      prompts++
      reply = listener
      code = requestCode
    }

    fun answer(granted: Boolean, requestCode: Int = code): Boolean {
      val result = if (granted) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED
      return reply?.onRequestPermissionsResult(requestCode, emptyArray(), intArrayOf(result)) ?: false
    }
  }

  class PromptActivity : AndroidActivity(), PermissionAwareActivity {
    var ownsFocus = false
    private var reply: PermissionListener? = null
    private var code = 0

    override fun hasWindowFocus() = ownsFocus
    override fun checkSelfPermission(permission: String) = PackageManager.PERMISSION_DENIED

    override fun requestPermissions(permissions: Array<String>, requestCode: Int, listener: PermissionListener?) {
      reply = listener
      code = requestCode
    }

    fun allow() {
      reply?.onRequestPermissionsResult(code, emptyArray(), intArrayOf(PackageManager.PERMISSION_GRANTED))
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
  @Test fun expiresALostCallbackAndDoesNotSettleTheNewRequestWithAnOldResult() {
    val activity = Activity()
    val results = mutableListOf<Boolean>()
    QrCameraPermission.request(activity) { results += it }
    val expiredCode = activity.code

    shadowOf(Looper.getMainLooper()).idleFor(60, TimeUnit.SECONDS)
    assertEquals(listOf(false), results)
    QrCameraPermission.request(activity) { results += it }
    assertEquals(2, activity.prompts)
    assertFalse(activity.answer(false, expiredCode))
    activity.answer(true)
    assertEquals(listOf(false, true), results)
  }

  @Test fun readsTheCurrentGrantAfterAMissingCallback() {
    val activity = Activity()
    val results = mutableListOf<Boolean>()
    QrCameraPermission.request(activity) { results += it }
    activity.currentGrant = PackageManager.PERMISSION_GRANTED

    shadowOf(Looper.getMainLooper()).idleFor(60, TimeUnit.SECONDS)
    assertEquals(listOf(true), results)
  }

  @Test fun keepsAwaitingARealPromptAfterTheTimeoutDuration() {
    val activity = Robolectric.buildActivity(PromptActivity::class.java).setup().get()
    val results = mutableListOf<Boolean>()
    QrCameraPermission.request(activity) { results += it }
    val looper = shadowOf(Looper.getMainLooper())

    looper.idleFor(120, TimeUnit.SECONDS)
    assertEquals(emptyList<Boolean>(), results)
    activity.allow()
    assertEquals(listOf(true), results)
  }

  @Test fun startsTheLostCallbackDeadlineWhenWindowFocusReturns() {
    val activity = Robolectric.buildActivity(PromptActivity::class.java).setup().get()
    val results = mutableListOf<Boolean>()
    QrCameraPermission.request(activity) { results += it }
    val looper = shadowOf(Looper.getMainLooper())

    looper.idleFor(120, TimeUnit.SECONDS)
    activity.ownsFocus = true
    looper.idleFor(60, TimeUnit.SECONDS)
    assertEquals(emptyList<Boolean>(), results)
    looper.idleFor(1, TimeUnit.SECONDS)
    assertEquals(listOf(false), results)
  }

}

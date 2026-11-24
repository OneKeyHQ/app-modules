package com.margelo.nitro.qrscanner

import android.Manifest
import android.app.Activity as AndroidActivity
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
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

  private class RegistryHost(val legacy: Activity = Activity()) :
    PermissionAwareActivity by legacy, ActivityResultRegistryOwner, LifecycleOwner {
    override val lifecycle = LifecycleRegistry(this).apply { currentState = Lifecycle.State.RESUMED }
    private var requestCode: Int? = null
    var prompts = 0
      private set

    override val activityResultRegistry = object : ActivityResultRegistry() {
      override fun <I, O> onLaunch(
        requestCode: Int,
        contract: ActivityResultContract<I, O>,
        input: I,
        options: ActivityOptionsCompat?,
      ) {
        assertEquals(Manifest.permission.CAMERA, input)
        this@RegistryHost.requestCode = requestCode
        prompts++
      }
    }

    fun answer(granted: Boolean) {
      activityResultRegistry.dispatchResult(checkNotNull(requestCode), granted)
    }
  }

  @Test fun registryResultSurvivesOtherReactNativePermissionRequests() {
    val activity = RegistryHost()
    val results = mutableListOf<Boolean>()
    QrCameraPermission.request(activity) { results += it }
    QrCameraPermission.request(activity) { results += it }

    // Another permission uses ReactActivityDelegate's separate listener.
    activity.legacy.requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 42) { _, _, _ -> true }
    activity.legacy.answer(false)
    shadowOf(Looper.getMainLooper()).idleFor(6, TimeUnit.MINUTES)

    assertEquals(1, activity.prompts)
    assertEquals(emptyList<Boolean>(), results)
    activity.answer(true)
    assertEquals(listOf(true, true), results)
  }

  @Test fun registryDoesNotNotifyDisposedHosts() {
    val activity = RegistryHost()
    val results = mutableListOf<Boolean>()
    QrCameraPermission.request(activity) { results += it }

    activity.lifecycle.currentState = Lifecycle.State.DESTROYED
    activity.answer(true)
    assertEquals(emptyList<Boolean>(), results)
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

  @Test fun legacyDeadlineHasABackstopWhenMainWindowNeverRegainsFocus() {
    val activity = Robolectric.buildActivity(PromptActivity::class.java).setup().get()
    val results = mutableListOf<Boolean>()
    QrCameraPermission.request(activity) { results += it }
    shadowOf(Looper.getMainLooper()).idleFor(5, TimeUnit.MINUTES)
    assertEquals(listOf(false), results)
  }

}

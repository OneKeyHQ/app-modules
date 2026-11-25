package com.margelo.nitro.qrscanner

import android.Manifest
import android.app.Activity as AndroidActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.facebook.react.modules.core.PermissionAwareActivity
import com.margelo.nitro.qrscanner.QrCameraPermission.Result
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

  class PromptActivity : AndroidActivity(), PermissionAwareActivity, LifecycleOwner {
    override val lifecycle = LifecycleRegistry(this).apply { currentState = Lifecycle.State.RESUMED }
    var prompts = 0
      private set
    var ownsFocus = false
    private var reply: PermissionListener? = null
    private var code = 0

    override fun hasWindowFocus() = ownsFocus
    override fun checkSelfPermission(permission: String) = PackageManager.PERMISSION_DENIED

    override fun requestPermissions(permissions: Array<String>, requestCode: Int, listener: PermissionListener?) {
      reply = listener
      code = requestCode
      prompts++
    }

    fun cancelPrompt() {
      reply?.onRequestPermissionsResult(code, emptyArray(), intArrayOf())
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
        assertEquals(listOf(Manifest.permission.CAMERA), (input as Array<*>).toList())
        this@RegistryHost.requestCode = requestCode
        prompts++
      }
    }

    fun answer(granted: Boolean): Boolean {
      val result = if (granted) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED
      return dispatch(arrayOf(Manifest.permission.CAMERA), intArrayOf(result))
    }

    fun cancelPrompt() = dispatch(emptyArray(), intArrayOf())

    private fun dispatch(permissions: Array<String>, results: IntArray): Boolean {
      val intent = Intent()
        .putExtra(ActivityResultContracts.RequestMultiplePermissions.EXTRA_PERMISSIONS, permissions)
        .putExtra(ActivityResultContracts.RequestMultiplePermissions.EXTRA_PERMISSION_GRANT_RESULTS, results)
      val handled = activityResultRegistry.dispatchResult(checkNotNull(requestCode), AndroidActivity.RESULT_OK, intent)
      shadowOf(Looper.getMainLooper()).idle()
      return handled
    }
  }

  @Test fun registryResultSurvivesOtherReactNativePermissionRequests() {
    val activity = RegistryHost()
    val results = mutableListOf<Result>()
    QrCameraPermission.request(activity) { results += it }
    QrCameraPermission.request(activity) { results += it }

    // Another permission uses ReactActivityDelegate's separate listener.
    activity.legacy.requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 42) { _, _, _ -> true }
    activity.legacy.answer(false)
    shadowOf(Looper.getMainLooper()).idleFor(6, TimeUnit.MINUTES)

    assertEquals(1, activity.prompts)
    assertEquals(emptyList<Result>(), results)
    activity.answer(true)
    assertEquals(listOf(Result.GRANTED, Result.GRANTED), results)
    assertFalse(activity.answer(false))
  }

  @Test fun registryDoesNotNotifyDisposedHosts() {
    val activity = RegistryHost()
    val results = mutableListOf<Result>()
    QrCameraPermission.request(activity) { results += it }

    activity.lifecycle.currentState = Lifecycle.State.DESTROYED
    activity.answer(true)
    assertEquals(emptyList<Result>(), results)
  }

  @Test fun registryCancellationRetriesWithTheSameWaitersOnceResumed() {
    val activity = RegistryHost()
    val results = mutableListOf<Result>()
    val looper = shadowOf(Looper.getMainLooper())
    QrCameraPermission.request(activity) { results += it }

    activity.lifecycle.currentState = Lifecycle.State.STARTED
    activity.cancelPrompt()
    QrCameraPermission.request(activity) { results += it }
    looper.idleFor(3, TimeUnit.SECONDS)
    assertEquals(1, activity.prompts)
    assertEquals(emptyList<Result>(), results)

    activity.lifecycle.currentState = Lifecycle.State.RESUMED
    looper.idleFor(1, TimeUnit.SECONDS)
    assertEquals(2, activity.prompts)
    activity.answer(false)
    looper.idleFor(3, TimeUnit.SECONDS)
    assertEquals(2, activity.prompts)
    assertEquals(listOf(Result.DENIED, Result.DENIED), results)
  }

  @Test fun activityPermissionCanRetryWhenOnlyTheScannerWindowHasFocus() {
    val activity = Robolectric.buildActivity(PromptActivity::class.java).setup().get()
    val results = mutableListOf<Result>()
    val looper = shadowOf(Looper.getMainLooper())
    var scannerHasFocus = false
    QrCameraPermission.request(activity, { scannerHasFocus }) { results += it }
    activity.cancelPrompt()

    looper.idleFor(3, TimeUnit.SECONDS)
    assertEquals(1, activity.prompts)
    scannerHasFocus = true
    looper.idleFor(1, TimeUnit.SECONDS)
    assertEquals(2, activity.prompts)
    activity.allow()
    assertEquals(listOf(Result.GRANTED), results)
  }

  @Test fun deactivatingTheLastWaiterCancelsTheRetry() {
    val activity = RegistryHost()
    val cancel = QrCameraPermission.request(activity) { error("Cancelled waiter was called") }
    activity.cancelPrompt()
    cancel()

    shadowOf(Looper.getMainLooper()).idleFor(3, TimeUnit.SECONDS)
    assertEquals(1, activity.prompts)
  }

  @Test fun aBlockedRetryExpiresAsCancellation() {
    val activity = Robolectric.buildActivity(PromptActivity::class.java).setup().get()
    val results = mutableListOf<Result>()
    QrCameraPermission.request(activity) { results += it }
    activity.cancelPrompt()

    shadowOf(Looper.getMainLooper()).idleFor(5, TimeUnit.MINUTES)
    assertEquals(1, activity.prompts)
    assertEquals(listOf(Result.CANCELLED), results)
  }

  @Test fun sharesThePromptAndFansOutToActiveViews() {
    val activity = Activity()
    val results = mutableListOf<Pair<Int, Result>>()
    val cancel = QrCameraPermission.request(activity) { results += 1 to it }
    QrCameraPermission.request(activity) { results += 2 to it }
    QrCameraPermission.request(activity) { results += 3 to it }
    cancel()
    assertEquals(1, activity.prompts)
    activity.answer(true)
    assertEquals(listOf(2 to Result.GRANTED, 3 to Result.GRANTED), results)

    QrCameraPermission.request(activity) { results += 4 to it }
    assertEquals(2, activity.prompts)
    activity.answer(false)
    assertEquals(4 to Result.DENIED, results.last())
  }

  @Test fun expiresALostCallbackAndDoesNotSettleTheNewRequestWithAnOldResult() {
    val activity = Activity()
    val results = mutableListOf<Result>()
    QrCameraPermission.request(activity) { results += it }
    val expiredCode = activity.code

    shadowOf(Looper.getMainLooper()).idleFor(60, TimeUnit.SECONDS)
    assertEquals(listOf(Result.DENIED), results)
    QrCameraPermission.request(activity) { results += it }
    assertEquals(2, activity.prompts)
    assertFalse(activity.answer(false, expiredCode))
    activity.answer(true)
    assertEquals(listOf(Result.DENIED, Result.GRANTED), results)
  }

  @Test fun readsTheCurrentGrantAfterAMissingCallback() {
    val activity = Activity()
    val results = mutableListOf<Result>()
    QrCameraPermission.request(activity) { results += it }
    activity.currentGrant = PackageManager.PERMISSION_GRANTED

    shadowOf(Looper.getMainLooper()).idleFor(60, TimeUnit.SECONDS)
    assertEquals(listOf(Result.GRANTED), results)
  }

  @Test fun keepsAwaitingARealPromptAfterTheTimeoutDuration() {
    val activity = Robolectric.buildActivity(PromptActivity::class.java).setup().get()
    val results = mutableListOf<Result>()
    QrCameraPermission.request(activity) { results += it }
    val looper = shadowOf(Looper.getMainLooper())

    looper.idleFor(120, TimeUnit.SECONDS)
    assertEquals(emptyList<Result>(), results)
    activity.allow()
    assertEquals(listOf(Result.GRANTED), results)
  }

  @Test fun startsTheLostCallbackDeadlineWhenWindowFocusReturns() {
    val activity = Robolectric.buildActivity(PromptActivity::class.java).setup().get()
    val results = mutableListOf<Result>()
    QrCameraPermission.request(activity) { results += it }
    val looper = shadowOf(Looper.getMainLooper())

    looper.idleFor(120, TimeUnit.SECONDS)
    assertEquals(emptyList<Result>(), results)
    activity.ownsFocus = true
    activity.lifecycle.currentState = Lifecycle.State.STARTED
    looper.idleFor(70, TimeUnit.SECONDS)
    assertEquals(emptyList<Result>(), results)
    activity.lifecycle.currentState = Lifecycle.State.RESUMED
    looper.idleFor(60, TimeUnit.SECONDS)
    assertEquals(emptyList<Result>(), results)
    looper.idleFor(1, TimeUnit.SECONDS)
    assertEquals(listOf(Result.DENIED), results)
  }

  @Test fun legacyDeadlineHasABackstopWhenMainWindowNeverRegainsFocus() {
    val activity = Robolectric.buildActivity(PromptActivity::class.java).setup().get()
    val results = mutableListOf<Result>()
    QrCameraPermission.request(activity) { results += it }
    shadowOf(Looper.getMainLooper()).idleFor(5, TimeUnit.MINUTES)
    assertEquals(listOf(Result.DENIED), results)
  }
}

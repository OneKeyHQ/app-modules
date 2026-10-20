// Adapted from react-native-capture-protection 2.3.0 by lethe; see LICENSE.upstream.
package com.margelo.nitro.reactnativecaptureprotection

import android.app.Activity
import android.app.Application
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Display
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.ReactApplication
import com.margelo.nitro.NitroModules
import com.margelo.nitro.core.Promise
import java.lang.ref.WeakReference
import java.util.WeakHashMap
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

@DoNotStrip
class ReactNativeCaptureProtection : HybridReactNativeCaptureProtectionSpec() {
  private val owner = nextOwner.incrementAndGet()
  private var listener: ((Double) -> Unit)? = null

  override fun prevent(): Promise<Unit> = onMain {
    val context = NitroModules.applicationContext
      ?: throw IllegalStateException("React context is unavailable.")
    val app = context.applicationContext as Application
    val activity = CaptureProtectionCoordinator.foregroundActivity(app)
      ?: throw IllegalStateException("No foreground activity is available.")
    CaptureProtectionCoordinator.prevent(owner, app, activity)
  }

  override fun allow(): Promise<Unit> = onMain {
    CaptureProtectionCoordinator.allow(owner)
  }

  override fun setListener(callback: ((Double) -> Unit)?) {
    mainSync {
      if (callback == null) {
        listener = null
        CaptureProtectionCoordinator.unsubscribe(owner)
      } else {
        val context = NitroModules.applicationContext
          ?: throw IllegalStateException("React context is unavailable.")
        val weak = WeakReference(this)
        val app = context.applicationContext as Application
        CaptureProtectionCoordinator.subscribe(
          owner, app, CaptureProtectionCoordinator.foregroundActivity(app)
        ) { event -> weak.get()?.listener?.invoke(event) }
        listener = callback
        CaptureProtectionCoordinator.emitRecordingState(owner)
      }
    }
  }

  override fun dispose() {
    release()
    super.dispose()
  }

  @Suppress("unused")
  protected fun finalize() {
    release()
  }

  private fun release() {
    mainHandler.post {
      listener = null
      CaptureProtectionCoordinator.unsubscribe(owner)
      CaptureProtectionCoordinator.allow(owner)
    }
  }

  private fun onMain(operation: () -> Unit): Promise<Unit> {
    val promise = Promise<Unit>()
    mainHandler.post {
      try {
        operation()
        promise.resolve(Unit)
      } catch (error: Exception) {
        promise.reject(error)
      }
    }
    return promise
  }

  private fun mainSync(operation: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
      operation()
    } else {
      val task = FutureTask(operation, Unit)
      mainHandler.post(task)
      try {
        task.get(5, TimeUnit.SECONDS)
      } finally {
        task.cancel(false)
      }
    }
  }

  companion object {
    private val nextOwner = AtomicLong()
    private val mainHandler = Handler(Looper.getMainLooper())
  }
}

private object CaptureProtectionCoordinator : Application.ActivityLifecycleCallbacks,
  DisplayManager.DisplayListener {
  private val handler = Handler(Looper.getMainLooper())
  private val owners = mutableSetOf<Long>()
  private val listeners = mutableMapOf<Long, (Double) -> Unit>()
  private val originalFlags = WeakHashMap<Activity, Boolean>()
  private val displays = mutableSetOf<Int>()
  private var application: Application? = null
  private var displayManager: DisplayManager? = null
  private var currentActivity: WeakReference<Activity>? = null
  private var callbackActivity: WeakReference<Activity>? = null
  private var screenshotCallback: Any? = null

  fun foregroundActivity(app: Application): Activity? {
    // Nitro's global React context can belong to the background runtime.
    // The application ReactHost is the UI host; lifecycle tracking survives recreation.
    return (app as? ReactApplication)?.reactHost?.currentReactContext?.currentActivity
      ?: currentActivity?.get()
  }

  private fun checkCapacity(owner: Long) {
    val identities = owners + listeners.keys
    check(owner in identities || identities.size < 32) {
      "Capture protection supports at most 32 native owners."
    }
  }

  fun prevent(owner: Long, app: Application, activity: Activity) {
    checkCapacity(owner)
    check(!activity.isFinishing && !activity.isDestroyed) { "Activity is unavailable." }
    protect(activity)
    owners.add(owner)
    start(app, activity)
  }

  fun allow(owner: Long) {
    owners.remove(owner)
    if (owners.isEmpty()) {
      for ((activity, wasSecure) in originalFlags.toMap()) {
        if (!wasSecure) activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
      }
      originalFlags.clear()
    }
    stopIfIdle()
  }

  fun subscribe(owner: Long, app: Application, activity: Activity?, callback: (Double) -> Unit) {
    checkCapacity(owner)
    listeners[owner] = callback
    start(app, activity)
  }

  fun unsubscribe(owner: Long) {
    listeners.remove(owner)
    stopIfIdle()
  }

  fun emitRecordingState(owner: Long) {
    if (displays.isNotEmpty()) listeners[owner]?.invoke(1.0)
  }

  private fun protect(activity: Activity) {
    if (!originalFlags.containsKey(activity)) {
      originalFlags[activity] = activity.window.attributes.flags and
        WindowManager.LayoutParams.FLAG_SECURE != 0
    }
    activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
  }

  private fun emit(event: Double) {
    for ((owner, callback) in listeners.toMap()) {
      if (listeners.containsKey(owner)) callback(event)
    }
  }

  private fun start(app: Application, activity: Activity?) {
    if (application == null) {
      application = app
      app.registerActivityLifecycleCallbacks(this)
      displayManager = app.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
      displays.addAll(displayManager!!.displays.map { it.displayId }
        .filter { it != Display.DEFAULT_DISPLAY })
      displayManager!!.registerDisplayListener(this, handler)
    }
    if (activity != null) {
      currentActivity = WeakReference(activity)
      registerScreenshot(activity)
    }
  }

  private fun stopIfIdle() {
    if (owners.isNotEmpty() || listeners.isNotEmpty()) return
    unregisterScreenshot()
    displayManager?.unregisterDisplayListener(this)
    application?.unregisterActivityLifecycleCallbacks(this)
    displayManager = null
    application = null
    currentActivity = null
    displays.clear()
  }

  private fun registerScreenshot(activity: Activity) {
    if (Build.VERSION.SDK_INT < 34 || callbackActivity?.get() === activity) return
    unregisterScreenshot()
    try {
      screenshotCallback = ScreenshotApi34.register(activity) { emit(3.0) }
      callbackActivity = WeakReference(activity)
    } catch (_: SecurityException) {
      // A missing normal screenshot-detection permission must not remove protection.
    }
  }

  private fun unregisterScreenshot() {
    if (Build.VERSION.SDK_INT >= 34) {
      val activity = callbackActivity?.get()
      val callback = screenshotCallback
      if (activity != null && callback != null) {
        try {
          ScreenshotApi34.unregister(activity, callback)
        } catch (_: IllegalArgumentException) {
          // The OS may have already disposed the activity's registration.
        }
      }
    }
    screenshotCallback = null
    callbackActivity = null
  }

  override fun onActivityResumed(activity: Activity) {
    currentActivity = WeakReference(activity)
    if (owners.isNotEmpty()) protect(activity)
    registerScreenshot(activity)
  }

  override fun onActivityPaused(activity: Activity) {
    if (callbackActivity?.get() === activity) unregisterScreenshot()
  }

  override fun onActivityDestroyed(activity: Activity) {
    if (callbackActivity?.get() === activity) unregisterScreenshot()
    if (currentActivity?.get() === activity) currentActivity = null
    originalFlags.remove(activity)
  }

  override fun onActivityCreated(activity: Activity, state: Bundle?) {}
  override fun onActivityStarted(activity: Activity) {}
  override fun onActivityStopped(activity: Activity) {}
  override fun onActivitySaveInstanceState(activity: Activity, state: Bundle) {}

  override fun onDisplayAdded(displayId: Int) {
    // Private MediaProjection displays can be absent from getDisplay() here.
    if (displayId != Display.DEFAULT_DISPLAY && displays.add(displayId)) emit(1.0)
  }

  override fun onDisplayRemoved(displayId: Int) {
    if (displays.remove(displayId) && displays.isEmpty()) emit(2.0)
  }

  override fun onDisplayChanged(displayId: Int) {}
}

@RequiresApi(34)
private object ScreenshotApi34 {
  fun register(activity: Activity, onScreenshot: () -> Unit): Any {
    val callback = Activity.ScreenCaptureCallback { onScreenshot() }
    activity.registerScreenCaptureCallback(activity.mainExecutor, callback)
    return callback
  }

  fun unregister(activity: Activity, callback: Any) {
    activity.unregisterScreenCaptureCallback(callback as Activity.ScreenCaptureCallback)
  }
}

package com.margelo.nitro.reactnativedeviceutils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.ReactApplicationContext
import com.margelo.nitro.NitroModules
import com.margelo.nitro.core.Promise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

@DoNotStrip
class ReactNativeDeviceUtils : HybridReactNativeDeviceUtilsSpec(), LifecycleEventListener {
  
  private var windowLayoutInfo: WindowLayoutInfo? = null
  private var isSpanning = false
  private var layoutInfoConsumer: Consumer<WindowLayoutInfo>? = null
  private var windowInfoTracker: WindowInfoTracker? = null
  private var spanningChangedListeners: MutableList<(Boolean) -> Unit> = mutableListOf()

   companion object {
    private var reactContext: ReactApplicationContext? = null
    
    fun setReactContext(context: ReactApplicationContext) {
      reactContext = context
    }
   }

    init {
        NitroModules.applicationContext?.let { ctx ->
            ctx.addLifecycleEventListener(this)
        } ?: run {
        }
    }

  private fun getContext(): Context? {
    return reactContext ?: getCurrentActivity()
  }
  
  private fun getCurrentActivity(): Activity? {
    return reactContext?.currentActivity
  }
  
  // MARK: - Dual Screen Detection
  
  override fun isDualScreenDevice(): Boolean {
    val activity = getCurrentActivity() ?: return false
    
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      return hasFoldingFeature(activity)
    }
    return false
  }
  
  @RequiresApi(Build.VERSION_CODES.R)
  private fun hasFoldingFeature(activity: Activity): Boolean {
    // Check if device has folding features using WindowManager library
    // This is the recommended approach for detecting foldable devices
    try {
      val windowInfoTracker = WindowInfoTracker.getOrCreate(activity)
      // If WindowInfoTracker is available, the device supports foldable features
      // We can also check for specific display features
      val displayFeatures = windowLayoutInfo?.displayFeatures
      if (displayFeatures != null) {
        for (feature in displayFeatures) {
          if (feature is FoldingFeature) {
            return true
          }
        }
      }
      // Even if no folding feature is currently detected, the device might still be foldable
      // Return true if WindowInfoTracker is available (indicates foldable support)
      return true
    } catch (e: Exception) {
      // WindowManager library not available or device doesn't support foldables
      return false
    }
  }
  
  override fun isSpanning(): Boolean {
    return this.isSpanning
  }
  
  private fun checkIsSpanning(layoutInfo: WindowLayoutInfo?): Boolean {
    if (layoutInfo == null) {
      return false
    }
    
    val foldingFeature = getFoldingFeature(layoutInfo)
    if (foldingFeature == null) {
      return false
    }
    
    // Consider spanning if the folding feature divides the screen
    return foldingFeature.state == FoldingFeature.State.FLAT ||
           foldingFeature.state == FoldingFeature.State.HALF_OPENED
  }
  
  private fun getFoldingFeature(layoutInfo: WindowLayoutInfo?): FoldingFeature? {
    if (layoutInfo == null) {
      return null
    }
    
    val features = layoutInfo.displayFeatures
    for (feature in features) {
      if (feature is FoldingFeature) {
        return feature
      }
    }
    return null
  }
  
  // MARK: - Window Information
  
  override fun getWindowRects(): Promise<Array<DualScreenInfoRect>> {
    return Promise.async {
      val activity = getCurrentActivity()
      if (activity == null || windowLayoutInfo == null) {
        return@async arrayOf()
      }
      
      val layoutInfo = windowLayoutInfo
      if (layoutInfo != null) {
        getWindowRectsFromLayoutInfo(activity, layoutInfo)
      } else {
        arrayOf()
      }
    }
  }
  
  private fun getWindowRectsFromLayoutInfo(activity: Activity, layoutInfo: WindowLayoutInfo): Array<DualScreenInfoRect> {
    val rects = mutableListOf<DualScreenInfoRect>()
    
    val foldingFeature = getFoldingFeature(layoutInfo)
    if (foldingFeature == null) {
      // No folding feature, return full screen rect
      val screenRect = Rect()
      activity.window.decorView.getWindowVisibleDisplayFrame(screenRect)
      rects.add(rectToDualScreenInfoRect(screenRect))
      return rects.toTypedArray()
    }
    
    // Split screen based on folding feature
    val hingeBounds = foldingFeature.bounds
    val screenRect = Rect()
    activity.window.decorView.getWindowVisibleDisplayFrame(screenRect)
    
    if (foldingFeature.orientation == FoldingFeature.Orientation.VERTICAL) {
      // Vertical fold - left and right screens
      val leftRect = Rect(screenRect.left, screenRect.top, hingeBounds.left, screenRect.bottom)
      val rightRect = Rect(hingeBounds.right, screenRect.top, screenRect.right, screenRect.bottom)
      rects.add(rectToDualScreenInfoRect(leftRect))
      rects.add(rectToDualScreenInfoRect(rightRect))
    } else {
      // Horizontal fold - top and bottom screens
      val topRect = Rect(screenRect.left, screenRect.top, screenRect.right, hingeBounds.top)
      val bottomRect = Rect(screenRect.left, hingeBounds.bottom, screenRect.right, screenRect.bottom)
      rects.add(rectToDualScreenInfoRect(topRect))
      rects.add(rectToDualScreenInfoRect(bottomRect))
    }
    
    return rects.toTypedArray()
  }
  
  private fun rectToDualScreenInfoRect(rect: Rect): DualScreenInfoRect {
    return DualScreenInfoRect(
      x = rect.left.toDouble(),
      y = rect.top.toDouble(),
      width = rect.width().toDouble(),
      height = rect.height().toDouble()
    )
  }
  
  override fun getHingeBounds(): Promise<DualScreenInfoRect> {
    return Promise.async {
      val layoutInfo = windowLayoutInfo
      if (layoutInfo == null) {
        return@async DualScreenInfoRect(x = 0.0, y = 0.0, width = 0.0, height = 0.0)
      }
      
      val foldingFeature = getFoldingFeature(layoutInfo)
      if (foldingFeature != null) {
        val bounds = foldingFeature.bounds
        DualScreenInfoRect(
          x = bounds.left.toDouble(),
          y = bounds.top.toDouble(),
          width = bounds.width().toDouble(),
          height = bounds.height().toDouble()
        )
      } else {
        DualScreenInfoRect(x = 0.0, y = 0.0, width = 0.0, height = 0.0)
      }
    }
  }

  fun callSpanningChangedListeners(isSpanning: Boolean) {
    for (listener in spanningChangedListeners) {
      listener(isSpanning)
    }
  }

  override fun addSpanningChangedListener(callback: (isSpanning: Boolean) -> Unit): () -> Unit {
    spanningChangedListeners.add(callback)
    return {
      spanningChangedListeners.remove(callback)
    }
  }

  private fun startObservingLayoutChanges() {
    val activity = getCurrentActivity() ?: return
    
    // Window Manager library requires API 24+, but full foldable support is API 30+
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      try {
        windowInfoTracker = WindowInfoTracker.getOrCreate(activity)
        
        // Create consumer for window layout info
        layoutInfoConsumer = Consumer<WindowLayoutInfo> { layoutInfo ->
          onWindowLayoutInfoChanged(layoutInfo)
        }
        
        // Use main executor for callbacks
        val mainExecutor: Executor = ContextCompat.getMainExecutor(activity)
        
        // Subscribe to window layout changes using the Java adapter
        val callbackAdapter = androidx.window.java.layout.WindowInfoTrackerCallbackAdapter(windowInfoTracker!!)
        
        callbackAdapter.addWindowLayoutInfoListener(
          activity,
          mainExecutor,
          layoutInfoConsumer!!
        )
      } catch (e: Exception) {
        // Window tracking not supported on this device/API level, ignore
      }
    }
  }
  
  private fun stopObservingLayoutChanges() {
    if (windowInfoTracker != null && layoutInfoConsumer != null) {
      try {
        // The listener will be cleaned up when the activity is destroyed
        layoutInfoConsumer = null
        windowInfoTracker = null
      } catch (e: Exception) {
        // Ignore cleanup errors
      }
    }
  }
  
  private fun onWindowLayoutInfoChanged(layoutInfo: WindowLayoutInfo) {
    this.windowLayoutInfo = layoutInfo
    
    val wasSpanning = this.isSpanning
    this.isSpanning = checkIsSpanning(layoutInfo)
    
    // Emit event if spanning state changed
    if (wasSpanning != this.isSpanning) {
      this.callSpanningChangedListeners(this.isSpanning)
    }
  }
  
  // MARK: - Background Color
  
  override fun changeBackgroundColor(r: Double, g: Double, b: Double, a: Double) {
    val activity = getCurrentActivity() ?: return
    activity.runOnUiThread {
      try {
        val rootView = activity.window.decorView
        rootView.rootView.setBackgroundColor(Color.rgb(r.toInt(), g.toInt(), b.toInt()))
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

    override fun onHostResume() {
        startObservingLayoutChanges()
    }

    override fun onHostPause() {
        stopObservingLayoutChanges()
    }

    override fun onHostDestroy() {
        stopObservingLayoutChanges()
    }
}

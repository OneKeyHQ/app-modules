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
import com.facebook.react.bridge.ReactApplicationContext
import com.margelo.nitro.core.Promise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

@DoNotStrip
class ReactNativeDeviceUtils : HybridReactNativeDeviceUtilsSpec() {
  
  private var spanningCallback: ((Boolean) -> Unit)? = null
  private var lastSpanningState = false
  private val coroutineScope = CoroutineScope(Dispatchers.Main)
  private var windowLayoutInfo: WindowLayoutInfo? = null
  private var isSpanning = false
  private var layoutInfoConsumer: Consumer<WindowLayoutInfo>? = null
  private var windowInfoTracker: WindowInfoTracker? = null
  
  companion object {
    private var reactContext: ReactApplicationContext? = null
    
    fun setReactContext(context: ReactApplicationContext) {
      reactContext = context
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
    // This is a best-effort check
    // In a real implementation, you might want to check device manufacturer and model
    return true // Assume support for now, actual spanning detection happens in runtime
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
    return Promise.async { resolve, reject ->
      coroutineScope.launch {
        try {
          val activity = getCurrentActivity()
          if (activity == null || windowLayoutInfo == null) {
            resolve(arrayOf())
            return@launch
          }
          
          val rects = getWindowRectsFromLayoutInfo(activity, windowLayoutInfo!!)
          resolve(rects)
        } catch (e: Exception) {
          reject(e)
        }
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
    return Promise.async { resolve, reject ->
      coroutineScope.launch {
        try {
          if (windowLayoutInfo == null) {
            resolve(DualScreenInfoRect(x = 0.0, y = 0.0, width = 0.0, height = 0.0))
            return@launch
          }
          
          val foldingFeature = getFoldingFeature(windowLayoutInfo)
          if (foldingFeature != null) {
            val bounds = foldingFeature.bounds
            val hingeRect = DualScreenInfoRect(
              x = bounds.left.toDouble(),
              y = bounds.top.toDouble(),
              width = bounds.width().toDouble(),
              height = bounds.height().toDouble()
            )
            resolve(hingeRect)
          } else {
            resolve(DualScreenInfoRect(x = 0.0, y = 0.0, width = 0.0, height = 0.0))
          }
        } catch (e: Exception) {
          reject(e)
        }
      }
    }
  }
  
  // MARK: - Spanning Change Callback
  
  override fun onSpanningChanged(callback: (Boolean) -> Unit) {
    this.spanningCallback = callback
    startObservingLayoutChanges()
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
        
        // Use Java-friendly callback approach
        val mainExecutor: Executor = activity.mainExecutor
        
        // Subscribe to window layout changes
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
      spanningCallback?.invoke(this.isSpanning)
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
}

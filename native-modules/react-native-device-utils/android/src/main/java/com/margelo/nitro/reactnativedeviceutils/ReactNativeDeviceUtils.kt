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
import androidx.core.content.ContextCompat
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.bridge.ReactApplicationContext
import com.margelo.nitro.core.Promise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@DoNotStrip
class ReactNativeDeviceUtils : HybridReactNativeDeviceUtilsSpec() {
  
  private var spanningCallback: ((Boolean) -> Unit)? = null
  private var lastSpanningState = false
  private val coroutineScope = CoroutineScope(Dispatchers.Main)
  
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
    val context = getContext() ?: return false
    
    // Check for Microsoft Surface Duo or other dual screen devices
    val packageManager = context.packageManager
    
    // Surface Duo specific feature
    val hasDualScreen = packageManager.hasSystemFeature("com.microsoft.device.display.displaymask")
    
    // Alternative: Check for multiple displays
    val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    val displays = displayManager.displays
    
    return hasDualScreen || displays.size > 1
  }
  
  override fun isSpanning(): Boolean {
    val activity = getCurrentActivity() ?: return false
    
    try {
      // For Surface Duo, check if the app is spanning across both screens
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val windowMetrics = windowManager.currentWindowMetrics
        val bounds = windowMetrics.bounds
        
        // Get display metrics to compare
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        
        // If window width is significantly larger than display width, likely spanning
        return bounds.width() > displayMetrics.widthPixels * 1.5
      } else {
        // Fallback for older Android versions
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        
        val configuration = activity.resources.configuration
        return configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE &&
               configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
      }
    } catch (e: Exception) {
      return false
    }
  }
  
  // MARK: - Window Information
  
  override fun getWindowRects(): Promise<Array<DualScreenInfoRect>> {
    return Promise.async { resolve, reject ->
      coroutineScope.launch {
        try {
          val activity = getCurrentActivity()
          if (activity == null) {
            resolve(arrayOf())
            return@launch
          }
          
          val rects = mutableListOf<DualScreenInfoRect>()
          
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val windowMetrics = windowManager.currentWindowMetrics
            val bounds = windowMetrics.bounds
            
            if (isDualScreenDevice() && isSpanning()) {
              // For dual screen spanning, split the bounds
              val halfWidth = bounds.width() / 2
              
              // Left screen
              rects.add(DualScreenInfoRect(
                x = bounds.left.toDouble(),
                y = bounds.top.toDouble(),
                width = halfWidth.toDouble(),
                height = bounds.height().toDouble()
              ))
              
              // Right screen
              rects.add(DualScreenInfoRect(
                x = (bounds.left + halfWidth).toDouble(),
                y = bounds.top.toDouble(),
                width = halfWidth.toDouble(),
                height = bounds.height().toDouble()
              ))
            } else {
              // Single window
              rects.add(DualScreenInfoRect(
                x = bounds.left.toDouble(),
                y = bounds.top.toDouble(),
                width = bounds.width().toDouble(),
                height = bounds.height().toDouble()
              ))
            }
          } else {
            // Fallback for older versions
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            
            rects.add(DualScreenInfoRect(
              x = 0.0,
              y = 0.0,
              width = displayMetrics.widthPixels.toDouble(),
              height = displayMetrics.heightPixels.toDouble()
            ))
          }
          
          resolve(rects.toTypedArray())
        } catch (e: Exception) {
          reject(e)
        }
      }
    }
  }
  
  override fun getHingeBounds(): Promise<DualScreenInfoRect> {
    return Promise.async { resolve, reject ->
      coroutineScope.launch {
        try {
          val activity = getCurrentActivity()
          if (activity == null || !isDualScreenDevice()) {
            // No hinge if not a dual screen device
            resolve(DualScreenInfoRect(x = 0.0, y = 0.0, width = 0.0, height = 0.0))
            return@launch
          }
          
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val windowMetrics = windowManager.currentWindowMetrics
            val bounds = windowMetrics.bounds
            
            if (isSpanning()) {
              // Calculate hinge position (center of the spanning window)
              val hingeX = bounds.width() / 2.0 - 10.0 // 20dp wide hinge
              val hingeRect = DualScreenInfoRect(
                x = hingeX,
                y = bounds.top.toDouble(),
                width = 20.0, // Hinge width
                height = bounds.height().toDouble()
              )
              resolve(hingeRect)
            } else {
              resolve(DualScreenInfoRect(x = 0.0, y = 0.0, width = 0.0, height = 0.0))
            }
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
    
    // Start monitoring configuration changes
    startSpanningMonitor()
  }
  
  private fun startSpanningMonitor() {
    coroutineScope.launch {
      // Simple polling mechanism to detect spanning changes
      // In a real implementation, you might want to use configuration change listeners
      while (spanningCallback != null) {
        try {
          val currentSpanning = isSpanning()
          if (currentSpanning != lastSpanningState) {
            lastSpanningState = currentSpanning
            spanningCallback?.invoke(currentSpanning)
          }
          kotlinx.coroutines.delay(1000) // Check every second
        } catch (e: Exception) {
          // Handle error silently
        }
      }
    }
  }
  
  // MARK: - Background Color
  
  override fun changeBackgroundColor(color: String) {
    val activity = getCurrentActivity() ?: return
    
    activity.runOnUiThread {
      try {
        val parsedColor = parseColor(color)
        val window = activity.window
        window.statusBarColor = parsedColor
        window.navigationBarColor = parsedColor
        
        // Also set the content view background
        val contentView = window.decorView.findViewById<android.view.View>(android.R.id.content)
        contentView?.setBackgroundColor(parsedColor)
      } catch (e: Exception) {
        // Handle error silently
      }
    }
  }
  
  private fun parseColor(colorString: String): Int {
    val trimmedColor = colorString.trim()
    
    return try {
      when {
        trimmedColor.startsWith("#") -> {
          Color.parseColor(trimmedColor)
        }
        else -> {
          // Handle named colors
          when (trimmedColor.lowercase()) {
            "red" -> Color.RED
            "green" -> Color.GREEN
            "blue" -> Color.BLUE
            "yellow" -> Color.YELLOW
            "orange" -> Color.rgb(255, 165, 0)
            "purple" -> Color.rgb(128, 0, 128)
            "black" -> Color.BLACK
            "white" -> Color.WHITE
            "gray", "grey" -> Color.GRAY
            "transparent", "clear" -> Color.TRANSPARENT
            else -> Color.WHITE
          }
        }
      }
    } catch (e: Exception) {
      Color.WHITE
    }
  }
}

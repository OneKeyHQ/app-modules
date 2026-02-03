package com.margelo.nitro.reactnativedeviceutils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.preference.PreferenceManager
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import androidx.window.java.layout.WindowInfoTrackerCallbackAdapter
import androidx.appcompat.app.AppCompatDelegate
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.ReactApplicationContext
import com.margelo.nitro.NitroModules
import com.margelo.nitro.core.Promise
import java.util.concurrent.Executor
import java.util.concurrent.CopyOnWriteArrayList

data class Listener(
  val id: Double,
  val callback: (Boolean) -> Unit
)

@DoNotStrip
class ReactNativeDeviceUtils : HybridReactNativeDeviceUtilsSpec(), LifecycleEventListener {

  /**
   * Foldable device model constants for various manufacturers.
   * Reference: https://storage.googleapis.com/play_public/supported_devices.html
   */
  companion object {
    private const val PREF_KEY_FOLDABLE = "1k_fold"
    private const val PREF_KEY_UI_STYLE = "1k_user_interface_style"

    // Xiaomi foldable models
    private val XIAOMI_FOLDABLE_MODELS = setOf(
      "M2011J18C",   // Mi MIX FOLD
      "22061218C",   // MIX FOLD 2
      "2308CPXD0C",  // MIX FOLD 3
      "24072PX77C",  // MIX FOLD 4
      "2405CPX3DC",  // MIX FLIP
      "2405CPX3DG"   // MIX FLIP
    )

    // Huawei foldable models
    private val HUAWEI_FOLDABLE_MODELS = setOf(
      "TAH-AN00", "TAH-AN00m", "TAH-N29m",  // Mate X
      "GRL-AL10",                            // Mate X3
      "TET-AN50",                            // Mate Xs
      "PAL-AL00", "PAL-LX9",                 // Mate Xs 2
      "ICL-AL20", "ICL-AL10",                // Pocket S
      "BAL-AL00", "BAL-L49", "BAL-AL60",     // Pocket 2
      "PSD-AL00",                            // Mate X5
      "LEM-AL00",                            // Mate X6
      "ALT-AL10", "ALT-AL00", "ALT-L29",     // Pocket
      "TGW-AL00", "TGW-L29",                 // Mate X5
      "TWH-AL10",                            // Mate X3
      "DHF-AL00", "DHF-LX9",                 // Mate Xs 3
      "RHA-AN00m"                            // Magic V series
    )

    // Huawei foldable device codes
    private val HUAWEI_FOLDABLE_DEVICES = setOf(
      "HWTAH", "HWMRX", "HWTET", "HWPAL", "MateX"
    )

    // Vivo foldable models
    private val VIVO_FOLDABLE_MODELS = setOf(
      "V2337A",  // X Fold3 Pro
      "V2330",   // X Fold3 Pro
      "V2178A",  // X Fold
      "V2229A",  // X Fold+
      "V2266A",  // vivo X Fold2
      "V2303A",  // vivo X Fold3
      "V2256A",  // X Flip
      "V2436A",  // X Fold5
      "V2429"    // X Fold5
    )

    // OPPO foldable models
    private val OPPO_FOLDABLE_MODELS = setOf(
      // Find N5
      "PKH110", "CPH2671",
      // Find N5 卫星通信版
      "PKH120",
      // Find N3
      "PHN110", "CPH2499",
      // Find N3 Flip
      "PHT110", "CPH2519",
      // Find N2 Flip
      "CPH2437", "PGT110",
      // Find N
      "PEUM00"
    )

    // Samsung foldable models
    private val SAMSUNG_FOLDABLE_MODELS = setOf(
      // Galaxy Fold
      "SCV44", "SM-F9000", "SM-F900F", "SM-F900U", "SM-F900U1", "SM-F900W",
      // Galaxy Fold 5G
      "SM-F907B", "SM-F907N",
      // Galaxy Z Fold2 5G
      "SM-F9160", "SM-F916B", "SM-F916N", "SM-F916Q", "SM-F916U", "SM-F916U1", "SM-F916W",
      // Galaxy Z Fold3 5G
      "SC-55B", "SCG11", "SM-F9260", "SM-F926B", "SM-F926N", "SM-F926U", "SM-F926U1", "SM-F926W",
      // Galaxy Z Fold4
      "SC-55C", "SCG16", "SM-F9360", "SM-F936B", "SM-F936N", "SM-F936U", "SM-F936U1", "SM-F936W",
      // Galaxy Z Fold5
      "SC-55D", "SCG22", "SM-F9460", "SM-F946B", "SM-F946N", "SM-F946Q", "SM-F946U", "SM-F946U1", "SM-F946W",
      // Galaxy Z Fold6
      "SC-55E", "SCG28", "SM-F9560", "SM-F956B", "SM-F956N", "SM-F956Q", "SM-F956U", "SM-F956U1", "SM-F956W",
      // Galaxy Z Fold7
      "SC-56F", "SCG34", "SM-F9660", "SM-F966B", "SM-F966N", "SM-F966Q", "SM-F966U", "SM-F966U1", "SM-F966W", "SM-F966Z",
      // Galaxy Z Fold Special Edition
      "SM-F958N",
      // Galaxy Z TriFold
      "SM-F9680", "SM-F968B", "SM-F968N", "SM-F968U1",
      // Galaxy Z Flip
      "SCV47", "SM-F7000", "SM-F700F", "SM-F700N", "SM-F700U", "SM-F700U1", "SM-F700W",
      // Galaxy Z Flip 5G
      "SCG04", "SM-F7070", "SM-F707B", "SM-F707N", "SM-F707U", "SM-F707U1", "SM-F707W",
      // Galaxy Z Flip3 5G
      "SC-54B", "SCG12", "SM-F7110", "SM-F711B", "SM-F711N", "SM-F711U", "SM-F711U1", "SM-F711W",
      // Galaxy Z Flip4
      "SC-54C", "SCG17", "SM-F7210", "SM-F721B", "SM-F721C", "SM-F721N", "SM-F721U", "SM-F721U1", "SM-F721W",
      // Galaxy Z Flip5
      "SC-54D", "SCG23", "SM-F7310", "SM-F731B", "SM-F731N", "SM-F731Q", "SM-F731U", "SM-F731U1", "SM-F731W",
      // Galaxy Z Flip6
      "SC-54E", "SCG29", "SM-F7410", "SM-F741B", "SM-F741N", "SM-F741Q", "SM-F741U", "SM-F741U1", "SM-F741W",
      // Galaxy Z Flip7
      "SC-55F", "SCG35", "SM-F7660", "SM-F766B", "SM-F766N", "SM-F766Q", "SM-F766U", "SM-F766U1", "SM-F766W", "SM-F766Z",
      // Galaxy Z Flip7 FE
      "SM-F7610", "SM-F761B", "SM-F761N", "SM-F761U", "SM-F761U1",
      // 心系天下 W23/W25 Flip
      "SM-W7023", "SM-W7025"
    )

    // Samsung foldable model prefixes
    private val SAMSUNG_FOLDABLE_PREFIXES = listOf(
      "SM-F9",  // Galaxy Z Fold series
      "SM-F7",  // Galaxy Z Flip series
      "SM-W70"  // 心系天下 W series Flip
    )

    // Google foldable models
    private val GOOGLE_FOLDABLE_MODELS = setOf(
      "Pixel Fold",
      "Pixel 9 Pro Fold",
      "Pixel 10 Pro Fold"
    )

    // Motorola foldable models
    private val MOTOROLA_FOLDABLE_MODELS = setOf(
      // razr 40 series
      "XT2323-3",  // moto razr 40
      "XT2321-2",  // moto razr 40 Ultra
      // razr 50 series
      "XT2451-4",  // moto razr 50
      "XT2453-2",  // motorola razr 50
      // razr 60 series
      "XT2551-3",  // motorola razr 60 ultra
      // razr 2022
      "XT2251-1",
      // razr Japan models
      "M-51E",     // motorola razr 50d
      "M-51F"      // motorola razr 60d
    )

    // ZTE/Nubia foldable models
    private val ZTE_FOLDABLE_MODELS = setOf(
      "NX732J",   // nubia Flip 2 5G
      "NX724J",   // nubia Flip 5G
      "Z9900S",   // nubia Fold
      "A502ZT",   // nubia Fold (carrier)
      "A304ZT",   // Libero Flip
      "Z8900CA"   // nubia Flip 2 5G
    )

    // Tecno foldable models
    private val TECNO_FOLDABLE_MODELS = setOf(
      "TECNO-AD10", "TECNO AD10",  // PHANTOM V Fold
      "TECNO-AE10", "TECNO AE10",  // PHANTOM V Fold2 5G
      "TECNO-AD11", "TECNO AD11",  // PHANTOM V Flip 5G
      "TECNO-AE11", "TECNO AE11"   // PHANTOM V Flip2 5G
    )

    // Infinix foldable models
    private val INFINIX_FOLDABLE_MODELS = setOf(
      "Infinix-X6962", "Infinix X6962"  // ZERO Flip
    )

    // Royole foldable models
    private val ROYOLE_FOLDABLE_MODELS = setOf(
      "RY1205"  // VERTU Ayxta Fold 3
    )

    // Honor foldable models
    private val HONOR_FOLDABLE_MODELS = setOf(
      "VER-N49",    // HONOR Magic V2
      "VER-N49DP",  // PORSCHE DESIGN HONOR Magic V2 RSR
      "FCP-N49",    // HONOR Magic V3
      "MBH-N49",    // HONOR Magic V5
      "FRI-NX9"     // HONOR Magic Vs
    )

    // Vertu foldable models
    private val VERTU_FOLDABLE_MODELS = setOf(
      "VTL-202302"  // METAFLIP
    )
  }

  private var windowLayoutInfo: WindowLayoutInfo? = null
  private var isSpanning = false
  private var layoutInfoConsumer: Consumer<WindowLayoutInfo>? = null
  private var windowInfoTracker: WindowInfoTracker? = null
  private var callbackAdapter: WindowInfoTrackerCallbackAdapter? = null
  private var spanningChangedListeners: MutableList<Listener> = CopyOnWriteArrayList()
  private var isObservingLayoutChanges = false
  private var nextListenerId = 0.0
  private var isDualScreenDeviceDetected: Boolean? = null

  init {
      NitroModules.applicationContext?.let { ctx ->
          ctx.addLifecycleEventListener(this)
      }
      restoreUserInterfaceStyle()
  }

  private fun restoreUserInterfaceStyle() {
    try {
      val context = NitroModules.applicationContext ?: return
      val prefs = PreferenceManager.getDefaultSharedPreferences(context)
      val style = prefs.getString(PREF_KEY_UI_STYLE, null) ?: return
      applyUserInterfaceStyle(style)
    } catch (e: Exception) {
      // Ignore restore errors
    }
  }

  private fun applyUserInterfaceStyle(style: String) {
    val mode = when (style) {
      "light" -> AppCompatDelegate.MODE_NIGHT_NO
      "dark" -> AppCompatDelegate.MODE_NIGHT_YES
      else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
    AppCompatDelegate.setDefaultNightMode(mode)
  }
  
  private fun getCurrentActivity(): Activity? {
    return NitroModules.applicationContext?.currentActivity
  }

  override fun initEventListeners() {
    startObservingLayoutChanges()
  }

    // MARK: - Dual Screen Detection
  
  override fun isDualScreenDevice(): Boolean {
    // Check cached value from PreferenceManager first
    val cached = getCachedFoldableStatus()
    if (cached == true) {
      isDualScreenDeviceDetected = true
      return true
    }

    if (isDualScreenDeviceDetected != null) {
      return isDualScreenDeviceDetected!!
    }

    val activity = getCurrentActivity() ?: return false
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val hasFolding = hasFoldingFeature(activity)
      if (hasFolding) {
        saveFoldableStatus(true)
      }
      isDualScreenDeviceDetected = hasFolding
      return isDualScreenDeviceDetected!!
    }
    isDualScreenDeviceDetected = false
    return isDualScreenDeviceDetected!!
  }
  // MARK: - Manufacturer-specific Foldable Detection

  /**
   * Check if the device is a Xiaomi foldable
   * Detection: Model list matching + system property check
   */
  private fun isXiaomiFoldable(): Boolean {
    val manufacturer = Build.MANUFACTURER.uppercase()
    if (manufacturer != "XIAOMI") return false

    val model = Build.MODEL.uppercase()
    if (XIAOMI_FOLDABLE_MODELS.contains(model)) return true

    // Fallback: Check system property for foldable type
    try {
      val clazz = Class.forName("android.os.SystemProperties")
      val method = clazz.getMethod("get", String::class.java)
      val value = method.invoke(null, "persist.sys.muiltdisplay_type") as? String
      if (value == "2") return true
    } catch (e: Exception) {
      // Ignore reflection errors
    }
    return false
  }

  /**
   * Check if the device is a Huawei foldable
   * Detection: Model list + device code + system feature check
   */
  private fun isHuaweiFoldable(): Boolean {
    val manufacturer = Build.MANUFACTURER.uppercase()
    if (manufacturer != "HUAWEI") return false

    val model = Build.MODEL.uppercase()
    val device = Build.DEVICE.uppercase()

    // Check model list
    if (HUAWEI_FOLDABLE_MODELS.any { model.contains(it.uppercase()) }) return true

    // Check device codes
    if (HUAWEI_FOLDABLE_DEVICES.any { device.contains(it.uppercase()) }) return true

    // Fallback: Check system feature for posture sensor
    try {
      val context = NitroModules.applicationContext
      if (context != null) {
        val pm = context.packageManager
        if (pm.hasSystemFeature("com.huawei.hardware.sensor.posture")) {
          return true
        }
      }
    } catch (e: Exception) {
      // Ignore
    }
    return false
  }

  /**
   * Check if the device is a Vivo foldable
   * Detection: Model list + private API check
   */
  private fun isVivoFoldable(): Boolean {
    val manufacturer = Build.MANUFACTURER.uppercase()
    if (manufacturer != "VIVO") return false

    val model = Build.MODEL.uppercase()
    if (VIVO_FOLDABLE_MODELS.contains(model)) return true

    // Fallback: Check using FtDeviceInfo API
    try {
      val clazz = Class.forName("android.util.FtDeviceInfo")
      val method = clazz.getMethod("getDeviceType")
      val deviceType = method.invoke(null) as? String
      if (deviceType?.lowercase() == "foldable") return true
    } catch (e: Exception) {
      // Ignore reflection errors
    }
    return false
  }

  /**
   * Check if the device is an OPPO foldable
   * Detection: Model list + feature config check
   */
  private fun isOppoFoldable(): Boolean {
    val manufacturer = Build.MANUFACTURER.uppercase()
    if (manufacturer != "OPPO") return false

    val model = Build.MODEL.uppercase()
    if (OPPO_FOLDABLE_MODELS.contains(model)) return true

    // Fallback: Check using OplusFeatureConfigManager
    try {
      val clazz = Class.forName("com.oplus.content.OplusFeatureConfigManager")
      val getInstanceMethod = clazz.getMethod("getInstance")
      val instance = getInstanceMethod.invoke(null)
      val hasFeatureMethod = clazz.getMethod("hasFeature", String::class.java)
      val hasFeature = hasFeatureMethod.invoke(instance, "oplus.hardware.type.fold") as? Boolean
      if (hasFeature == true) return true
    } catch (e: Exception) {
      // Ignore reflection errors
    }
    return false
  }

  /**
   * Check if the device is a Samsung foldable
   * Detection: Model prefix matching + model list
   */
  private fun isSamsungFoldable(): Boolean {
    val manufacturer = Build.MANUFACTURER.uppercase()
    if (manufacturer != "SAMSUNG") return false

    val model = Build.MODEL.uppercase()

    // Check model prefixes (SM-F9xxx for Fold, SM-F7xxx for Flip)
    for (prefix in SAMSUNG_FOLDABLE_PREFIXES) {
      if (model.startsWith(prefix.uppercase())) return true
    }

    // Check Japan carrier model list
    if (SAMSUNG_FOLDABLE_MODELS.contains(model)) return true

    return false
  }

  /**
   * Check if the device is a Google foldable
   */
  private fun isGoogleFoldable(): Boolean {
    val manufacturer = Build.MANUFACTURER.uppercase()
    if (manufacturer != "GOOGLE") return false

    val model = Build.MODEL.uppercase()
    return GOOGLE_FOLDABLE_MODELS.any { model.contains(it.uppercase()) }
  }

  /**
   * Check if the device is a Motorola foldable
   */
  private fun isMotorolaFoldable(): Boolean {
    val manufacturer = Build.MANUFACTURER.uppercase()
    if (manufacturer != "MOTOROLA") return false

    val model = Build.MODEL.uppercase()
    return MOTOROLA_FOLDABLE_MODELS.contains(model)
  }

  /**
   * Check if the device is a ZTE/Nubia foldable
   */
  private fun isZteFoldable(): Boolean {
    val manufacturer = Build.MANUFACTURER.uppercase()
    if (manufacturer != "ZTE" && manufacturer != "NUBIA") return false

    val model = Build.MODEL.uppercase()
    return ZTE_FOLDABLE_MODELS.contains(model)
  }

  /**
   * Check if the device is a Tecno foldable
   */
  private fun isTecnoFoldable(): Boolean {
    val manufacturer = Build.MANUFACTURER.uppercase()
    if (manufacturer != "TECNO") return false

    val model = Build.MODEL.uppercase()
    return TECNO_FOLDABLE_MODELS.any { model.contains(it.uppercase()) }
  }

  /**
   * Check if the device is an Infinix foldable
   */
  private fun isInfinixFoldable(): Boolean {
    val manufacturer = Build.MANUFACTURER.uppercase()
    if (manufacturer != "INFINIX") return false

    val model = Build.MODEL.uppercase()
    return INFINIX_FOLDABLE_MODELS.any { model.contains(it.uppercase()) }
  }

  /**
   * Check if the device is a Royole foldable
   */
  private fun isRoyoleFoldable(): Boolean {
    val manufacturer = Build.MANUFACTURER.uppercase()
    if (manufacturer != "ROYOLE") return false

    val model = Build.MODEL.uppercase()
    return ROYOLE_FOLDABLE_MODELS.contains(model)
  }

  /**
   * Check if the device is an Honor foldable
   */
  private fun isHonorFoldable(): Boolean {
    val manufacturer = Build.MANUFACTURER.uppercase()
    if (manufacturer != "HONOR") return false

    val model = Build.MODEL.uppercase()
    return HONOR_FOLDABLE_MODELS.contains(model)
  }

  /**
   * Check if the device is a Vertu foldable
   */
  private fun isVertuFoldable(): Boolean {
    val manufacturer = Build.MANUFACTURER.uppercase()
    if (manufacturer != "VERTU") return false

    val model = Build.MODEL.uppercase()
    return VERTU_FOLDABLE_MODELS.contains(model)
  }

  /**
   * Get cached foldable status from PreferenceManager
   */
  private fun getCachedFoldableStatus(): Boolean? {
    try {
      val context = NitroModules.applicationContext ?: return null
      val prefs = PreferenceManager.getDefaultSharedPreferences(context)
      if (!prefs.contains(PREF_KEY_FOLDABLE)) return null
      return prefs.getBoolean(PREF_KEY_FOLDABLE, false)
    } catch (e: Exception) {
      return null
    }
  }

  /**
   * Save foldable status to PreferenceManager
   */
  private fun saveFoldableStatus(isFoldable: Boolean) {
    try {
      val context = NitroModules.applicationContext ?: return
      val prefs = PreferenceManager.getDefaultSharedPreferences(context)
      prefs.edit().putBoolean(PREF_KEY_FOLDABLE, isFoldable).apply()
    } catch (e: Exception) {
      // Ignore save errors
    }
  }

  /**
   * Detect if device is foldable by checking manufacturer-specific methods
   * Results are cached in PreferenceManager with key "1k_fold"
   */
  private fun isFoldableDeviceByName(): Boolean {
    // Check each manufacturer
    val isFoldable = isXiaomiFoldable() ||
                     isHuaweiFoldable() ||
                     isVivoFoldable() ||
                     isOppoFoldable() ||
                     isSamsungFoldable() ||
                     isGoogleFoldable() ||
                     isMotorolaFoldable() ||
                     isZteFoldable() ||
                     isTecnoFoldable() ||
                     isInfinixFoldable() ||
                     isRoyoleFoldable() ||
                     isHonorFoldable() ||
                     isVertuFoldable()

    // Cache the result
    return isFoldable
  }
  
  private fun hasFoldingFeature(activity: Activity): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
      return isFoldableDeviceByName()
    }
    
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
      // Check device model name to determine if it's a foldable device
      return isFoldableDeviceByName()
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
    // Create a snapshot to avoid ConcurrentModificationException when listeners modify the list during callbacks
    for (listener in spanningChangedListeners.toList()) {
      listener.callback(isSpanning)
    }
  }

  override fun addSpanningChangedListener(callback: (isSpanning: Boolean) -> Unit): Double = synchronized(this) {
    val id = nextListenerId
    nextListenerId++
    val listener = Listener(id, callback)
    spanningChangedListeners.add(listener)
    return@synchronized id
  }

    override fun removeSpanningChangedListener(id: Double) {
        spanningChangedListeners.removeIf { it.id == id }
    }

  private fun startObservingLayoutChanges() {
    if (isObservingLayoutChanges) {
      return
    }
    isObservingLayoutChanges = true
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
        callbackAdapter = WindowInfoTrackerCallbackAdapter(windowInfoTracker!!)
        
        if (callbackAdapter == null) {
          return
        }
        callbackAdapter!!.addWindowLayoutInfoListener(
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
    if (!isObservingLayoutChanges) {
      return
    }
    isObservingLayoutChanges = false

    // Properly clean up window layout listener
    try {
      if (callbackAdapter != null && layoutInfoConsumer != null) {
        callbackAdapter!!.removeWindowLayoutInfoListener(layoutInfoConsumer!!)
      }
    } catch (e: Exception) {
      // Ignore cleanup errors
    } finally {
      callbackAdapter = null
      layoutInfoConsumer = null
      windowInfoTracker = null
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
  
  // MARK: - User Interface Style

  override fun setUserInterfaceStyle(style: String) {
    try {
      val context = NitroModules.applicationContext
      if (context != null) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putString(PREF_KEY_UI_STYLE, style).apply()
      }
    } catch (e: Exception) {
      // Ignore save errors
    }
    applyUserInterfaceStyle(style)
  }

  // MARK: - Background Color
  
  override fun changeBackgroundColor(r: Double, g: Double, b: Double, a: Double) {
    val activity = getCurrentActivity() ?: return
    activity.runOnUiThread {
      try {
        // Clamp color values to valid range [0, 255]
        val red = Math.max(0, Math.min(255, r.toInt()))
        val green = Math.max(0, Math.min(255, g.toInt()))
        val blue = Math.max(0, Math.min(255, b.toInt()))
        val alpha = Math.max(0, Math.min(255, a.toInt()))

        val rootView = activity.window.decorView
        rootView.rootView.setBackgroundColor(Color.argb(alpha, red, green, blue))
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

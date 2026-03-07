package com.margelo.nitro.tabview

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap
import com.facebook.react.uimanager.ThemedReactContext
import com.rcttabview.ReactBottomNavigationView
import com.rcttabview.TabInfo

@DoNotStrip
class HybridTabView(val context: ThemedReactContext) : HybridTabViewSpec() {

  private val bottomNav = ReactBottomNavigationView(context)

  override val view: View = bottomNav

  init {
    bottomNav.onTabSelectedListener = { key ->
      onPageSelected?.invoke(key)
    }
    bottomNav.onTabLongPressedListener = { key ->
      onTabLongPress?.invoke(key)
    }
    bottomNav.onNativeLayoutListener = { width, height ->
      onNativeLayout?.invoke(width, height)
    }
    bottomNav.onTabBarMeasuredListener = { height ->
      onTabBarMeasured?.invoke(height.toDouble())
    }
  }

  // MARK: - Props

  override var items: Array<TabItemStruct>? = null
    set(value) {
      field = value
      value?.let { updateItemsFromStruct(it) }
    }

  override var selectedPage: String? = null
    set(value) {
      field = value
      value?.let { bottomNav.setSelectedItem(it) }
    }

  override var icons: Array<IconSourceStruct>? = null
    set(value) {
      field = value
      value?.let { updateIconsFromStruct(it) }
    }

  override var labeled: Boolean? = null
    set(value) {
      field = value
      bottomNav.setLabeled(value)
    }

  override var sidebarAdaptable: Boolean? = null
    set(value) { field = value } // iOS only

  override var disablePageAnimations: Boolean? = null
    set(value) {
      field = value
      bottomNav.disablePageAnimations = value ?: false
    }

  override var hapticFeedbackEnabled: Boolean? = null
    set(value) {
      field = value
      bottomNav.isHapticFeedbackEnabled = value ?: false
    }

  override var scrollEdgeAppearance: String? = null
    set(value) { field = value } // iOS only

  override var minimizeBehavior: String? = null
    set(value) { field = value } // iOS only

  override var tabBarHidden: Boolean? = null
    set(value) {
      field = value
      bottomNav.setTabBarHidden(value ?: false)
    }

  override var translucent: Boolean? = null
    set(value) { field = value } // iOS only

  override var barTintColor: String? = null
    set(value) {
      field = value
      value?.let { bottomNav.setBarTintColor(parseColor(it)) }
    }

  override var activeTintColor: String? = null
    set(value) {
      field = value
      value?.let { bottomNav.setActiveTintColor(parseColor(it)) }
    }

  override var inactiveTintColor: String? = null
    set(value) {
      field = value
      value?.let { bottomNav.setInactiveTintColor(parseColor(it)) }
    }

  override var rippleColor: String? = null
    set(value) {
      field = value
      value?.let { bottomNav.setRippleColor(ColorStateList.valueOf(parseColor(it))) }
    }

  override var activeIndicatorColor: String? = null
    set(value) {
      field = value
      value?.let { bottomNav.setActiveIndicatorColor(ColorStateList.valueOf(parseColor(it))) }
    }

  override var fontFamily: String? = null
    set(value) {
      field = value
      bottomNav.setFontFamily(value)
    }

  override var fontWeight: String? = null
    set(value) {
      field = value
      bottomNav.setFontWeight(value)
    }

  override var fontSize: Double? = null
    set(value) {
      field = value
      value?.let { bottomNav.setFontSize(it.toInt()) }
    }

  // MARK: - Events

  override var onPageSelected: ((key: String) -> Unit)? = null
  override var onTabLongPress: ((key: String) -> Unit)? = null
  override var onTabBarMeasured: ((height: Double) -> Unit)? = null
  override var onNativeLayout: ((width: Double, height: Double) -> Unit)? = null

  // MARK: - Methods

  override fun insertChild(tag: Double, index: Double) {
    // Child view management is handled by the custom ViewGroupManager
  }

  override fun removeChild(tag: Double, index: Double) {
    // Child view management is handled by the custom ViewGroupManager
  }

  // MARK: - Helpers

  private fun updateItemsFromStruct(itemStructs: Array<TabItemStruct>) {
    val itemsArray = itemStructs.map { item ->
      TabInfo(
        key = item.key,
        title = item.title,
        badge = item.badge,
        badgeBackgroundColor = item.badgeBackgroundColor?.toInt(),
        badgeTextColor = item.badgeTextColor?.toInt(),
        activeTintColor = item.activeTintColor?.toInt(),
        hidden = item.hidden ?: false,
        testID = item.testID
      )
    }.toMutableList()
    bottomNav.updateItems(itemsArray)
  }

  private fun updateIconsFromStruct(iconStructs: Array<IconSourceStruct>) {
    // Build a ReadableArray-compatible structure for the existing setIcons method
    val iconsArray = WritableNativeArray()
    for (source in iconStructs) {
      val map = WritableNativeMap()
      map.putString("uri", source.uri)
      map.putDouble("width", source.width)
      map.putDouble("height", source.height)
      map.putDouble("scale", source.scale)
      iconsArray.pushMap(map)
    }
    bottomNav.setIcons(iconsArray)
  }

  private fun parseColor(hex: String): Int {
    return try {
      Color.parseColor(hex)
    } catch (e: Exception) {
      Color.TRANSPARENT
    }
  }

  fun onDropViewInstance() {
    bottomNav.onDropViewInstance()
  }
}

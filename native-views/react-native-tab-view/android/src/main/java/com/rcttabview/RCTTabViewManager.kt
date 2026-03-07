package com.rcttabview

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.events.Event
import com.facebook.react.uimanager.events.EventDispatcher

class TabEvent(
  private val surfId: Int,
  private val viewId: Int,
  private val name: String,
  private val data: WritableMap
) : Event<TabEvent>(surfId, viewId) {
  override fun getEventName(): String = name
  override fun getEventData(): WritableMap = data
}

class RCTTabViewManager : ViewGroupManager<ReactBottomNavigationView>() {

  override fun getName(): String = "RCTTabView"

  override fun createViewInstance(reactContext: ThemedReactContext): ReactBottomNavigationView {
    val view = ReactBottomNavigationView(reactContext)

    view.onTabSelectedListener = { key ->
      sendEvent(reactContext, view, "onPageSelected", Arguments.createMap().apply {
        putString("key", key)
      })
    }
    view.onTabLongPressedListener = { key ->
      sendEvent(reactContext, view, "onTabLongPress", Arguments.createMap().apply {
        putString("key", key)
      })
    }
    view.onTabBarMeasuredListener = { height ->
      sendEvent(reactContext, view, "onTabBarMeasured", Arguments.createMap().apply {
        putDouble("height", height.toDouble())
      })
    }
    view.onNativeLayoutListener = { width, height ->
      sendEvent(reactContext, view, "onNativeLayout", Arguments.createMap().apply {
        putDouble("width", width)
        putDouble("height", height)
      })
    }

    return view
  }

  private fun sendEvent(context: ThemedReactContext, view: View, eventName: String, params: WritableMap) {
    val dispatcher = UIManagerHelper.getEventDispatcherForReactTag(context, view.id)
    dispatcher?.dispatchEvent(TabEvent(UIManagerHelper.getSurfaceId(context), view.id, eventName, params))
  }

  override fun onDropViewInstance(view: ReactBottomNavigationView) {
    super.onDropViewInstance(view)
    view.onDropViewInstance()
  }

  // MARK: - Props

  @ReactProp(name = "items")
  fun setItems(view: ReactBottomNavigationView, items: ReadableArray?) {
    items ?: return
    val tabItems = mutableListOf<TabInfo>()
    for (i in 0 until items.size()) {
      val map = items.getMap(i) ?: continue
      tabItems.add(
        TabInfo(
          key = map.getString("key") ?: "",
          title = map.getString("title") ?: "",
          badge = if (map.hasKey("badge")) map.getString("badge") else null,
          badgeBackgroundColor = if (map.hasKey("badgeBackgroundColor")) map.getInt("badgeBackgroundColor") else null,
          badgeTextColor = if (map.hasKey("badgeTextColor")) map.getInt("badgeTextColor") else null,
          activeTintColor = if (map.hasKey("activeTintColor")) map.getInt("activeTintColor") else null,
          hidden = if (map.hasKey("hidden")) map.getBoolean("hidden") else false,
          testID = if (map.hasKey("testID")) map.getString("testID") else null
        )
      )
    }
    view.updateItems(tabItems)
  }

  @ReactProp(name = "selectedPage")
  fun setSelectedPage(view: ReactBottomNavigationView, selectedPage: String?) {
    selectedPage?.let { view.setSelectedItem(it) }
  }

  @ReactProp(name = "icons")
  fun setIcons(view: ReactBottomNavigationView, icons: ReadableArray?) {
    view.setIcons(icons)
  }

  @ReactProp(name = "labeled")
  fun setLabeled(view: ReactBottomNavigationView, labeled: Boolean?) {
    view.setLabeled(labeled)
  }

  @ReactProp(name = "disablePageAnimations")
  fun setDisablePageAnimations(view: ReactBottomNavigationView, disable: Boolean) {
    view.disablePageAnimations = disable
  }

  @ReactProp(name = "hapticFeedbackEnabled")
  fun setHapticFeedbackEnabled(view: ReactBottomNavigationView, enabled: Boolean) {
    view.isHapticFeedbackEnabled = enabled
  }

  @ReactProp(name = "tabBarHidden")
  fun setTabBarHidden(view: ReactBottomNavigationView, hidden: Boolean) {
    view.setTabBarHidden(hidden)
  }

  @ReactProp(name = "ignoreBottomInsets")
  fun setIgnoreBottomInsets(view: ReactBottomNavigationView, ignore: Boolean) {
    view.setIgnoreBottomInsets(ignore)
  }

  @ReactProp(name = "barTintColor")
  fun setBarTintColor(view: ReactBottomNavigationView, color: String?) {
    color?.let { view.setBarTintColor(parseColor(it)) }
  }

  @ReactProp(name = "activeTintColor")
  fun setActiveTintColor(view: ReactBottomNavigationView, color: String?) {
    color?.let { view.setActiveTintColor(parseColor(it)) }
  }

  @ReactProp(name = "inactiveTintColor")
  fun setInactiveTintColor(view: ReactBottomNavigationView, color: String?) {
    color?.let { view.setInactiveTintColor(parseColor(it)) }
  }

  @ReactProp(name = "rippleColor")
  fun setRippleColor(view: ReactBottomNavigationView, color: String?) {
    color?.let { view.setRippleColor(ColorStateList.valueOf(parseColor(it))) }
  }

  @ReactProp(name = "activeIndicatorColor")
  fun setActiveIndicatorColor(view: ReactBottomNavigationView, color: String?) {
    color?.let { view.setActiveIndicatorColor(ColorStateList.valueOf(parseColor(it))) }
  }

  @ReactProp(name = "fontFamily")
  fun setFontFamily(view: ReactBottomNavigationView, family: String?) {
    view.setFontFamily(family)
  }

  @ReactProp(name = "fontWeight")
  fun setFontWeight(view: ReactBottomNavigationView, weight: String?) {
    view.setFontWeight(weight)
  }

  @ReactProp(name = "fontSize")
  fun setFontSize(view: ReactBottomNavigationView, size: Int) {
    view.setFontSize(size)
  }

  // iOS-only props (no-ops on Android)
  @ReactProp(name = "sidebarAdaptable")
  fun setSidebarAdaptable(view: ReactBottomNavigationView, value: Boolean) {}

  @ReactProp(name = "scrollEdgeAppearance")
  fun setScrollEdgeAppearance(view: ReactBottomNavigationView, value: String?) {}

  @ReactProp(name = "minimizeBehavior")
  fun setMinimizeBehavior(view: ReactBottomNavigationView, value: String?) {}

  @ReactProp(name = "translucent")
  fun setTranslucent(view: ReactBottomNavigationView, value: Boolean) {}

  // MARK: - Events

  override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Any>? {
    return MapBuilder.of(
      "onPageSelected",
      MapBuilder.of("registrationName", "onPageSelected"),
      "onTabLongPress",
      MapBuilder.of("registrationName", "onTabLongPress"),
      "onTabBarMeasured",
      MapBuilder.of("registrationName", "onTabBarMeasured"),
      "onNativeLayout",
      MapBuilder.of("registrationName", "onNativeLayout")
    )
  }

  // MARK: - Child view management

  override fun addView(parent: ReactBottomNavigationView, child: View, index: Int) {
    parent.addView(child, index)
  }

  override fun getChildCount(parent: ReactBottomNavigationView): Int {
    return parent.layoutHolder.childCount
  }

  override fun getChildAt(parent: ReactBottomNavigationView, index: Int): View? {
    val container = parent.layoutHolder.getChildAt(index) as? ViewGroup
    return container?.getChildAt(0) ?: parent.layoutHolder.getChildAt(index)
  }

  override fun removeView(parent: ReactBottomNavigationView, view: View) {
    // Find the container that wraps this view
    for (i in 0 until parent.layoutHolder.childCount) {
      val container = parent.layoutHolder.getChildAt(i) as? ViewGroup
      if (container != null && container.childCount > 0 && container.getChildAt(0) === view) {
        parent.layoutHolder.removeViewAt(i)
        return
      }
    }
    parent.layoutHolder.removeView(view)
  }

  override fun removeAllViews(parent: ReactBottomNavigationView) {
    parent.layoutHolder.removeAllViews()
  }

  override fun removeViewAt(parent: ReactBottomNavigationView, index: Int) {
    parent.layoutHolder.removeViewAt(index)
  }

  override fun needsCustomLayoutForChildren(): Boolean = true

  // MARK: - Helpers

  private fun parseColor(hex: String): Int {
    return try {
      Color.parseColor(hex)
    } catch (e: Exception) {
      Color.TRANSPARENT
    }
  }
}

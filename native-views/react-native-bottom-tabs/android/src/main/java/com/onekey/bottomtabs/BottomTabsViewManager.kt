package com.onekey.bottomtabs

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.viewmanagers.RNBottomTabsViewManagerDelegate
import com.facebook.react.viewmanagers.RNBottomTabsViewManagerInterface
import com.onekey.bottomtabs.events.OnNativeLayoutEvent
import com.onekey.bottomtabs.events.OnTabBarMeasuredEvent
import com.onekey.bottomtabs.events.PageSelectedEvent
import com.onekey.bottomtabs.events.TabDoublePressEvent

@ReactModule(name = BottomTabsViewManager.NAME)
class BottomTabsViewManager(context: ReactApplicationContext) :
  ViewGroupManager<BottomTabsView>(),
  RNBottomTabsViewManagerInterface<BottomTabsView> {

  private val delegate: RNBottomTabsViewManagerDelegate<BottomTabsView, BottomTabsViewManager> =
    RNBottomTabsViewManagerDelegate(this)

  override fun createViewInstance(context: ThemedReactContext): BottomTabsView {
    val view = BottomTabsView(context)
    val eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(context, view.id)
    val surfaceId = UIManagerHelper.getSurfaceId(context)

    view.onTabSelectedListener = { key ->
      eventDispatcher?.dispatchEvent(PageSelectedEvent(surfaceId, view.id, key))
    }

    view.onTabDoublePressedListener = { key ->
      eventDispatcher?.dispatchEvent(TabDoublePressEvent(surfaceId, view.id, key))
    }

    view.onNativeLayoutListener = { width, height ->
      eventDispatcher?.dispatchEvent(OnNativeLayoutEvent(surfaceId, view.id, width, height))
    }

    view.onTabBarMeasuredListener = { height ->
      eventDispatcher?.dispatchEvent(OnTabBarMeasuredEvent(surfaceId, view.id, height))
    }

    return view
  }

  override fun onDropViewInstance(view: BottomTabsView) {
    super.onDropViewInstance(view)
    view.onDropViewInstance()
  }

  override fun getName(): String = NAME

  override fun getDelegate(): ViewManagerDelegate<BottomTabsView> = delegate

  // MARK: - Child view management

  override fun addView(parent: BottomTabsView, child: View, index: Int) {
    parent.addView(child, index)
  }

  override fun getChildCount(parent: BottomTabsView): Int {
    return parent.layoutHolder.childCount
  }

  override fun getChildAt(parent: BottomTabsView, index: Int): View? {
    val container = parent.layoutHolder.getChildAt(index) as? ViewGroup
    return container?.getChildAt(0) ?: parent.layoutHolder.getChildAt(index)
  }

  override fun removeView(parent: BottomTabsView, view: View) {
    for (i in 0 until parent.layoutHolder.childCount) {
      val container = parent.layoutHolder.getChildAt(i) as? ViewGroup
      if (container != null && container.childCount > 0 && container.getChildAt(0) === view) {
        parent.layoutHolder.removeViewAt(i)
        return
      }
    }
    parent.layoutHolder.removeView(view)
  }

  override fun removeAllViews(parent: BottomTabsView) {
    parent.layoutHolder.removeAllViews()
  }

  override fun removeViewAt(parent: BottomTabsView, index: Int) {
    parent.layoutHolder.removeViewAt(index)
  }

  override fun needsCustomLayoutForChildren(): Boolean = true

  // MARK: - Props (RNBottomTabsViewManagerInterface)

  override fun setItems(view: BottomTabsView?, value: ReadableArray?) {
    if (view != null && value != null) {
      val itemsArray = mutableListOf<TabInfo>()
      for (i in 0 until value.size()) {
        value.getMap(i)?.let { item ->
          itemsArray.add(
            TabInfo(
              key = item.getString("key") ?: "",
              title = item.getString("title") ?: "",
              badge = if (item.hasKey("badge")) item.getString("badge") else null,
              badgeBackgroundColor = if (item.hasKey("badgeBackgroundColor")) item.getInt("badgeBackgroundColor") else null,
              badgeTextColor = if (item.hasKey("badgeTextColor")) item.getInt("badgeTextColor") else null,
              activeTintColor = if (item.hasKey("activeTintColor")) item.getInt("activeTintColor") else null,
              hidden = if (item.hasKey("hidden")) item.getBoolean("hidden") else false,
              testID = item.getString("testID"),
              preventsDefault = if (item.hasKey("preventsDefault")) item.getBoolean("preventsDefault") else false
            )
          )
        }
      }
      view.updateItems(itemsArray)
    }
  }

  override fun setSelectedPage(view: BottomTabsView?, value: String?) {
    if (view != null && value != null)
      view.setSelectedItem(value)
  }

  override fun setIcons(view: BottomTabsView?, value: ReadableArray?) {
    if (view != null)
      view.setIcons(value)
  }

  override fun setLabeled(view: BottomTabsView?, value: Boolean) {
    if (view != null)
      view.setLabeled(value)
  }

  override fun setRippleColor(view: BottomTabsView?, value: Int?) {
    if (view != null && value != null) {
      val color = ColorStateList.valueOf(value)
      view.setRippleColor(color)
    }
  }

  override fun setBarTintColor(view: BottomTabsView?, value: Int?) {
    if (view != null && value != null)
      view.setBarTintColor(value)
  }

  override fun setActiveTintColor(view: BottomTabsView?, value: Int?) {
    if (view != null && value != null)
      view.setActiveTintColor(value)
  }

  override fun setInactiveTintColor(view: BottomTabsView?, value: Int?) {
    if (view != null && value != null)
      view.setInactiveTintColor(value)
  }

  override fun setActiveIndicatorColor(view: BottomTabsView?, value: Int?) {
    if (view != null && value != null) {
      val color = ColorStateList.valueOf(value)
      view.setActiveIndicatorColor(color)
    }
  }

  override fun setHapticFeedbackEnabled(view: BottomTabsView?, value: Boolean) {
    if (view != null)
      view.isHapticFeedbackEnabled = value
  }

  override fun setFontFamily(view: BottomTabsView?, value: String?) {
    view?.setFontFamily(value)
  }

  override fun setFontWeight(view: BottomTabsView?, value: String?) {
    view?.setFontWeight(value)
  }

  override fun setFontSize(view: BottomTabsView?, value: Int) {
    view?.setFontSize(value)
  }

  override fun setDisablePageAnimations(view: BottomTabsView?, value: Boolean) {
    view?.disablePageAnimations = value
  }

  override fun setTabBarHidden(view: BottomTabsView?, value: Boolean) {
    view?.setTabBarHidden(value)
  }

  override fun setIgnoreBottomInsets(view: BottomTabsView?, value: Boolean) {
    view?.setIgnoreBottomInsets(value)
  }

  override fun setAndroidTabBarStyle(view: BottomTabsView?, value: String?) {
    view?.setAndroidTabBarStyle(value)
  }

  override fun setIgnoreKeyboardInsets(view: BottomTabsView?, value: Boolean) {
    view?.setIgnoreKeyboardInsets(value)
  }

  override fun setAndroidBottomBlurEnabled(view: BottomTabsView?, value: Boolean) {
    view?.setAndroidBottomBlurEnabled(value)
  }

  // iOS-only props (no-ops on Android)
  // selectedIcons feeds UITabBarItem.selectedImage on iOS; Android keeps
  // swapping the icon in JS via the focusedKey-driven `icons` array.
  override fun setSelectedIcons(view: BottomTabsView?, value: ReadableArray?) {}
  override fun setTranslucent(view: BottomTabsView?, value: Boolean) {}
  override fun setSidebarAdaptable(view: BottomTabsView?, value: Boolean) {}
  override fun setScrollEdgeAppearance(view: BottomTabsView?, value: String?) {}
  override fun setMinimizeBehavior(view: BottomTabsView?, value: String?) {}
  override fun setSideAccessoryIcon(view: BottomTabsView?, value: ReadableMap?) {}
  override fun setSideAccessoryLabel(view: BottomTabsView?, value: String?) {}

  companion object {
    const val NAME = "RNBottomTabsView"
  }
}

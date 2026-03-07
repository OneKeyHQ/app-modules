package com.rcttabview

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.viewmanagers.RNCTabViewManagerDelegate
import com.facebook.react.viewmanagers.RNCTabViewManagerInterface
import com.rcttabview.events.OnNativeLayoutEvent
import com.rcttabview.events.OnTabBarMeasuredEvent
import com.rcttabview.events.PageSelectedEvent
import com.rcttabview.events.TabLongPressEvent

@ReactModule(name = RCTTabViewManager.NAME)
class RCTTabViewManager(context: ReactApplicationContext) :
  ViewGroupManager<ReactBottomNavigationView>(),
  RNCTabViewManagerInterface<ReactBottomNavigationView> {

  private val delegate: RNCTabViewManagerDelegate<ReactBottomNavigationView, RCTTabViewManager> =
    RNCTabViewManagerDelegate(this)

  override fun createViewInstance(context: ThemedReactContext): ReactBottomNavigationView {
    val view = ReactBottomNavigationView(context)
    val eventDispatcher = UIManagerHelper.getEventDispatcherForReactTag(context, view.id)
    val surfaceId = UIManagerHelper.getSurfaceId(context)

    view.onTabSelectedListener = { key ->
      eventDispatcher?.dispatchEvent(PageSelectedEvent(surfaceId, view.id, key))
    }

    view.onTabLongPressedListener = { key ->
      eventDispatcher?.dispatchEvent(TabLongPressEvent(surfaceId, view.id, key))
    }

    view.onNativeLayoutListener = { width, height ->
      eventDispatcher?.dispatchEvent(OnNativeLayoutEvent(surfaceId, view.id, width, height))
    }

    view.onTabBarMeasuredListener = { height ->
      eventDispatcher?.dispatchEvent(OnTabBarMeasuredEvent(surfaceId, view.id, height))
    }

    return view
  }

  override fun onDropViewInstance(view: ReactBottomNavigationView) {
    super.onDropViewInstance(view)
    view.onDropViewInstance()
  }

  override fun getName(): String = NAME

  override fun getDelegate(): ViewManagerDelegate<ReactBottomNavigationView> = delegate

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

  // MARK: - Props (RNCTabViewManagerInterface)

  override fun setItems(view: ReactBottomNavigationView?, value: ReadableArray?) {
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
              testID = item.getString("testID")
            )
          )
        }
      }
      view.updateItems(itemsArray)
    }
  }

  override fun setSelectedPage(view: ReactBottomNavigationView?, value: String?) {
    if (view != null && value != null)
      view.setSelectedItem(value)
  }

  override fun setIcons(view: ReactBottomNavigationView?, value: ReadableArray?) {
    if (view != null)
      view.setIcons(value)
  }

  override fun setLabeled(view: ReactBottomNavigationView?, value: Boolean) {
    if (view != null)
      view.setLabeled(value)
  }

  override fun setRippleColor(view: ReactBottomNavigationView?, value: Int?) {
    if (view != null && value != null) {
      val color = ColorStateList.valueOf(value)
      view.setRippleColor(color)
    }
  }

  override fun setBarTintColor(view: ReactBottomNavigationView?, value: Int?) {
    if (view != null && value != null)
      view.setBarTintColor(value)
  }

  override fun setActiveTintColor(view: ReactBottomNavigationView?, value: Int?) {
    if (view != null && value != null)
      view.setActiveTintColor(value)
  }

  override fun setInactiveTintColor(view: ReactBottomNavigationView?, value: Int?) {
    if (view != null && value != null)
      view.setInactiveTintColor(value)
  }

  override fun setActiveIndicatorColor(view: ReactBottomNavigationView?, value: Int?) {
    if (view != null && value != null) {
      val color = ColorStateList.valueOf(value)
      view.setActiveIndicatorColor(color)
    }
  }

  override fun setHapticFeedbackEnabled(view: ReactBottomNavigationView?, value: Boolean) {
    if (view != null)
      view.isHapticFeedbackEnabled = value
  }

  override fun setFontFamily(view: ReactBottomNavigationView?, value: String?) {
    view?.setFontFamily(value)
  }

  override fun setFontWeight(view: ReactBottomNavigationView?, value: String?) {
    view?.setFontWeight(value)
  }

  override fun setFontSize(view: ReactBottomNavigationView?, value: Int) {
    view?.setFontSize(value)
  }

  override fun setDisablePageAnimations(view: ReactBottomNavigationView?, value: Boolean) {
    view?.disablePageAnimations = value
  }

  override fun setTabBarHidden(view: ReactBottomNavigationView?, value: Boolean) {
    view?.setTabBarHidden(value)
  }

  override fun setIgnoreBottomInsets(view: ReactBottomNavigationView?, value: Boolean) {
    view?.setIgnoreBottomInsets(value)
  }

  // iOS-only props (no-ops on Android)
  override fun setTranslucent(view: ReactBottomNavigationView?, value: Boolean) {}
  override fun setSidebarAdaptable(view: ReactBottomNavigationView?, value: Boolean) {}
  override fun setScrollEdgeAppearance(view: ReactBottomNavigationView?, value: String?) {}
  override fun setMinimizeBehavior(view: ReactBottomNavigationView?, value: String?) {}

  companion object {
    const val NAME = "RNCTabView"
  }
}

package com.reactnativepagerview

import android.graphics.Color
import android.view.View
import com.facebook.react.bridge.ColorPropConverter
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableType
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.ReactStylesDiffMap
import com.facebook.react.uimanager.StateWrapper
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.RNCNativeScrollerManagerDelegate
import com.facebook.react.viewmanagers.RNCNativeScrollerManagerInterface

@ReactModule(name = NativeScrollerViewManager.NAME)
class NativeScrollerViewManager : ViewGroupManager<NativeScrollerView>(),
  RNCNativeScrollerManagerInterface<NativeScrollerView> {
  private val delegate: ViewManagerDelegate<NativeScrollerView> = RNCNativeScrollerManagerDelegate(this)
  override fun getDelegate() = delegate
  override fun getName() = NAME
  override fun createViewInstance(context: ThemedReactContext) = NativeScrollerView(context)
  override fun needsCustomLayoutForChildren() = false
  override fun addView(parent: NativeScrollerView, child: View, index: Int) = parent.addReactChild(child, index)
  override fun getChildCount(parent: NativeScrollerView) = parent.reactChildCount()
  override fun getChildAt(parent: NativeScrollerView, index: Int) = parent.reactChildAt(index)
  override fun removeViewAt(parent: NativeScrollerView, index: Int) {
    require(index == 0)
    parent.removeReactChild()
  }
  override fun removeAllViews(parent: NativeScrollerView) = parent.removeReactChild()
  override fun onDropViewInstance(view: NativeScrollerView) {
    view.viewport.stopMotion()
    view.setFabricState(null)
    super.onDropViewInstance(view)
  }
  override fun updateState(view: NativeScrollerView, props: ReactStylesDiffMap, stateWrapper: StateWrapper): Any? {
    view.setFabricState(stateWrapper)
    return null
  }
  override fun onAfterUpdateTransaction(view: NativeScrollerView) {
    super.onAfterUpdateTransaction(view)
    view.scheduleLayout()
  }

  @ReactProp(name = "contentWidth")
  override fun setContentWidth(view: NativeScrollerView, value: Double) = view.setContentSize(width = view.dip(value))
  @ReactProp(name = "contentHeight")
  override fun setContentHeight(view: NativeScrollerView, value: Double) = view.setContentSize(height = view.dip(value))
  @ReactProp(name = "scrollEnabled", defaultBoolean = true)
  override fun setScrollEnabled(view: NativeScrollerView, value: Boolean) = view.setScrollEnabled(value)
  @ReactProp(name = "nestedScrollEnabled", defaultBoolean = true)
  override fun setNestedScrollEnabled(view: NativeScrollerView, value: Boolean) {
    if (!value) view.viewport.stopMotion()
    view.viewport.isNestedScrollingEnabled = value
  }
  @ReactProp(name = "showsVerticalScrollIndicator", defaultBoolean = true)
  override fun setShowsVerticalScrollIndicator(view: NativeScrollerView, value: Boolean) {
    view.viewport.isVerticalScrollBarEnabled = value
  }
  @ReactProp(name = "showsHorizontalScrollIndicator")
  override fun setShowsHorizontalScrollIndicator(view: NativeScrollerView, value: Boolean) {
    view.viewport.isHorizontalScrollBarEnabled = value
  }
  // UIKit-only properties: Android keeps the platform NestedScrollView fling/edge policy.
  @ReactProp(name = "bounces", defaultBoolean = true)
  override fun setBounces(view: NativeScrollerView, value: Boolean) = Unit
  @ReactProp(name = "alwaysBounceVertical", defaultBoolean = true)
  override fun setAlwaysBounceVertical(view: NativeScrollerView, value: Boolean) = Unit
  @ReactProp(name = "decelerationRate", defaultDouble = 0.998)
  override fun setDecelerationRate(view: NativeScrollerView, value: Double) = Unit
  @ReactProp(name = "keyboardDismissMode")
  override fun setKeyboardDismissMode(view: NativeScrollerView, value: String?) {
    view.keyboardDismissMode = value ?: "none"
  }
  @ReactProp(name = "keyboardShouldPersistTaps")
  override fun setKeyboardShouldPersistTaps(view: NativeScrollerView, value: String?) {
    // The JS responder wrapper owns never/handled/always and TextInput exceptions.
    view.keyboardShouldPersistTaps = value ?: "never"
  }
  @ReactProp(name = "scrollEventThrottle")
  override fun setScrollEventThrottle(view: NativeScrollerView, value: Double) {
    view.scrollEventThrottle = value.coerceAtLeast(0.0)
  }
  @ReactProp(name = "contentInsetTop")
  override fun setContentInsetTop(view: NativeScrollerView, value: Double) { view.insetTop = view.dip(value).coerceAtLeast(0) }
  @ReactProp(name = "contentInsetRight")
  override fun setContentInsetRight(view: NativeScrollerView, value: Double) { view.insetRight = view.dip(value).coerceAtLeast(0) }
  @ReactProp(name = "contentInsetBottom")
  override fun setContentInsetBottom(view: NativeScrollerView, value: Double) { view.insetBottom = view.dip(value).coerceAtLeast(0) }
  @ReactProp(name = "contentInsetLeft")
  override fun setContentInsetLeft(view: NativeScrollerView, value: Double) { view.insetLeft = view.dip(value).coerceAtLeast(0) }
  @ReactProp(name = "refreshEnabled")
  override fun setRefreshEnabled(view: NativeScrollerView, value: Boolean) { view.refreshEnabled = value }
  @ReactProp(name = "refreshing")
  override fun setRefreshing(view: NativeScrollerView, value: Boolean) = view.setRefreshing(value)
  @ReactProp(name = "refreshTintColor", customType = "Color")
  override fun setRefreshTintColor(view: NativeScrollerView, value: Int?) {
    // Android's refreshColors determines the native arc; tintColor is iOS-only.
  }
  @ReactProp(name = "refreshColors", customType = "ColorArray")
  override fun setRefreshColors(view: NativeScrollerView, value: ReadableArray?) {
    if (value == null) {
      view.setRefreshColors(intArrayOf(Color.GRAY))
      return
    }
    val colors = ArrayList<Int>(value.size())
    for (index in 0 until value.size()) {
      // PlatformColor/DynamicColorIOS values arrive as maps, as in RN's SwipeRefreshLayoutManager.
      when (value.getType(index)) {
        ReadableType.Map -> ColorPropConverter.getColor(value.getMap(index), view.context)?.let(colors::add)
        ReadableType.Number -> colors.add(value.getInt(index))
        else -> Unit
      }
    }
    view.setRefreshColors(colors.toIntArray())
  }
  @ReactProp(name = "refreshProgressBackgroundColor", customType = "Color")
  override fun setRefreshProgressBackgroundColor(view: NativeScrollerView, value: Int?) =
    view.setRefreshBackground(value ?: Color.WHITE)
  @ReactProp(name = "refreshProgressViewOffset")
  override fun setRefreshProgressViewOffset(view: NativeScrollerView, value: Double) = view.setRefreshOffset(view.dip(value))
  @ReactProp(name = "refreshTitle")
  override fun setRefreshTitle(view: NativeScrollerView, value: String?) = Unit
  @ReactProp(name = "refreshTitleColor", customType = "Color")
  override fun setRefreshTitleColor(view: NativeScrollerView, value: Int?) = Unit
  @ReactProp(name = "refreshSize")
  override fun setRefreshSize(view: NativeScrollerView, value: String?) = view.setRefreshSize(value)

  override fun receiveCommand(view: NativeScrollerView, commandId: String, args: ReadableArray?) =
    delegate.receiveCommand(view, commandId, args)
  override fun setRefreshState(view: NativeScrollerView, refreshing: Boolean) = view.setRefreshing(refreshing)
  override fun scrollTo(view: NativeScrollerView, x: Double, y: Double, animated: Boolean) =
    view.scrollToPosition(view.dip(x), view.dip(y), animated)
  override fun scrollToEnd(view: NativeScrollerView, animated: Boolean) = view.scrollToEnd(animated)
  override fun flashScrollIndicators(view: NativeScrollerView) { view.viewport.flashIndicators() }

  override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Map<String, String>> =
    listOf("Scroll", "ScrollBeginDrag", "ScrollEndDrag", "MomentumScrollBegin", "MomentumScrollEnd", "Refresh", "ContentViewportChange")
      .associate { "top$it" to mapOf("registrationName" to "on$it") }.toMutableMap()

  companion object { const val NAME = "RNCNativeScroller" }
}

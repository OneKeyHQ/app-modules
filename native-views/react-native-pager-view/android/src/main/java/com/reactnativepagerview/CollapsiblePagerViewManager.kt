package com.reactnativepagerview

import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.common.MapBuilder
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.PixelUtil
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.RNCCollapsiblePagerViewManagerDelegate
import com.facebook.react.viewmanagers.RNCCollapsiblePagerViewManagerInterface
import com.reactnativepagerview.event.CollapsibleStateChangedEvent
import com.reactnativepagerview.event.PageScrollEvent
import com.reactnativepagerview.event.PageScrollStateChangedEvent
import com.reactnativepagerview.event.PageSelectedEvent
import org.json.JSONArray

@ReactModule(name = CollapsiblePagerViewManager.NAME)
class CollapsiblePagerViewManager : ViewGroupManager<CollapsiblePagerHost>(),
  RNCCollapsiblePagerViewManagerInterface<CollapsiblePagerHost> {

  private val delegate: ViewManagerDelegate<CollapsiblePagerHost> =
    RNCCollapsiblePagerViewManagerDelegate(this)

  override fun getDelegate() = delegate

  override fun getName() = NAME

  override fun createViewInstance(reactContext: ThemedReactContext): CollapsiblePagerHost {
    val host = CollapsiblePagerHost(reactContext)
    host.id = View.generateViewId()
    host.layoutParams = ViewGroup.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.MATCH_PARENT,
    )
    host.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
      override fun onPageScrolled(position: Int, offset: Float, offsetPixels: Int) {
        dispatch(reactContext, host, PageScrollEvent(host.id, position, offset))
      }

      override fun onPageSelected(position: Int) {
        host.selectPage(position)
        dispatch(reactContext, host, PageSelectedEvent(host.id, position))
        dispatchDiagnostics(reactContext, host, "page-selected")
      }

      override fun onPageScrollStateChanged(state: Int) {
        val value = when (state) {
          ViewPager2.SCROLL_STATE_IDLE -> "idle"
          ViewPager2.SCROLL_STATE_DRAGGING -> "dragging"
          ViewPager2.SCROLL_STATE_SETTLING -> "settling"
          else -> return
        }
        dispatch(reactContext, host, PageScrollStateChangedEvent(host.id, value))
        if (state == ViewPager2.SCROLL_STATE_IDLE) {
          dispatchDiagnostics(reactContext, host, "pager-idle")
        }
      }
    })
    host.onHeaderOffsetChanged = {
      if (!host.pager.isFakeDragging && host.pager.scrollState == ViewPager2.SCROLL_STATE_IDLE) {
        dispatchDiagnostics(reactContext, host, "vertical-idle")
      }
    }
    host.post {
      dispatch(reactContext, host, PageSelectedEvent(host.id, host.pager.currentItem))
      dispatchDiagnostics(reactContext, host, "mounted")
    }
    return host
  }

  private fun dispatch(reactContext: ThemedReactContext, host: CollapsiblePagerHost, event: com.facebook.react.uimanager.events.Event<*>) {
    UIManagerHelper.getEventDispatcherForReactTag(reactContext, host.id)?.dispatchEvent(event)
  }

  private fun dispatchDiagnostics(
    reactContext: ThemedReactContext,
    host: CollapsiblePagerHost,
    reason: String,
  ) {
    dispatch(
      reactContext,
      host,
      CollapsibleStateChangedEvent(
        host.id,
        host.selectedPage,
        PixelUtil.toDIPFromPixel(host.headerOffsetPx.toFloat()).toDouble(),
        host.adapter.itemCount,
        host.attachedPageCount(),
        host.observedScrollableCount(),
        host.retainedPages,
        reason,
      ),
    )
  }

  override fun addView(parent: CollapsiblePagerHost, child: View, index: Int) =
    parent.addReactChild(child, index)

  override fun getChildCount(parent: CollapsiblePagerHost) = parent.reactChildCount()

  override fun getChildAt(parent: CollapsiblePagerHost, index: Int) = parent.reactChildAt(index)

  override fun removeView(parent: CollapsiblePagerHost, view: View) = parent.removeReactChild(view)

  override fun removeViewAt(parent: CollapsiblePagerHost, index: Int) =
    parent.removeReactChild(parent.reactChildAt(index))

  override fun removeAllViews(parent: CollapsiblePagerHost) = parent.removeAllReactChildren()

  override fun needsCustomLayoutForChildren() = true

  @ReactProp(name = "scrollEnabled", defaultBoolean = true)
  override fun setScrollEnabled(view: CollapsiblePagerHost?, value: Boolean) {
    view?.pager?.isUserInputEnabled = value
  }

  @ReactProp(name = "layoutDirection")
  override fun setLayoutDirection(view: CollapsiblePagerHost?, value: String?) {
    view?.pager?.layoutDirection = if (value == "rtl") {
      View.LAYOUT_DIRECTION_RTL
    } else {
      View.LAYOUT_DIRECTION_LTR
    }
  }

  @ReactProp(name = "initialPage", defaultInt = 0)
  override fun setInitialPage(view: CollapsiblePagerHost?, value: Int) {
    view ?: return
    view.pendingInitialPage = value
    view.applyPendingInitialPage()
  }

  @ReactProp(name = "offscreenPageLimit", defaultInt = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT)
  override fun setOffscreenPageLimit(view: CollapsiblePagerHost?, value: Int) {
    view?.pager?.offscreenPageLimit = if (value == ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT) {
      value
    } else {
      value.coerceAtLeast(1)
    }
  }

  @ReactProp(name = "headerHeight", defaultInt = 0)
  override fun setHeaderHeight(view: CollapsiblePagerHost?, value: Int) {
    view?.headerHeightPx = PixelUtil.toPixelFromDIP(value.toDouble()).toInt()
  }

  @ReactProp(name = "stickyHeaderHeight", defaultInt = 0)
  override fun setStickyHeaderHeight(view: CollapsiblePagerHost?, value: Int) {
    view?.stickyHeaderHeightPx = PixelUtil.toPixelFromDIP(value.toDouble()).toInt()
  }

  @ReactProp(name = "pageKeys")
  override fun setPageKeys(view: CollapsiblePagerHost?, value: String?) {
    view ?: return
    view.pageKeys = parseStringArray(value)
  }

  @ReactProp(name = "retainedPages")
  override fun setRetainedPages(view: CollapsiblePagerHost?, value: String?) {
    view?.retainedPages = value ?: "[]"
  }

  private fun parseStringArray(value: String?): List<String> {
    if (value.isNullOrEmpty()) return emptyList()
    return runCatching {
      val json = JSONArray(value)
      List(json.length()) { index -> json.optString(index, "page-$index") }
    }.getOrDefault(emptyList())
  }

  override fun receiveCommand(root: CollapsiblePagerHost, commandId: String, args: ReadableArray?) {
    delegate.receiveCommand(root, commandId, args)
  }

  override fun setPage(view: CollapsiblePagerHost?, selectedPage: Int) {
    if (view != null && selectedPage in 0 until view.adapter.itemCount) {
      view.pager.setCurrentItem(selectedPage, true)
    }
  }

  override fun setPageWithoutAnimation(view: CollapsiblePagerHost?, selectedPage: Int) {
    if (view != null && selectedPage in 0 until view.adapter.itemCount) {
      view.pager.setCurrentItem(selectedPage, false)
    }
  }

  override fun setScrollEnabledImperatively(view: CollapsiblePagerHost?, scrollEnabled: Boolean) {
    view?.pager?.isUserInputEnabled = scrollEnabled
  }

  override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Map<String, String>> =
    MapBuilder.builder<String, Map<String, String>>()
      .put(PageScrollEvent.EVENT_NAME, MapBuilder.of("registrationName", "onPageScroll"))
      .put(PageSelectedEvent.EVENT_NAME, MapBuilder.of("registrationName", "onPageSelected"))
      .put(
        PageScrollStateChangedEvent.EVENT_NAME,
        MapBuilder.of("registrationName", "onPageScrollStateChanged"),
      )
      .put(
        CollapsibleStateChangedEvent.EVENT_NAME,
        MapBuilder.of("registrationName", "onCollapsibleStateChanged"),
      )
      .build()
      .toMutableMap()

  companion object {
    const val NAME = "RNCCollapsiblePagerView"
  }
}

package com.onekey.bottomtabs

import android.view.View
import com.facebook.react.bridge.Arguments
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.events.Event
import com.facebook.react.viewmanagers.RNBottomTabsDrawerManagerDelegate
import com.facebook.react.viewmanagers.RNBottomTabsDrawerManagerInterface

class BottomTabsDrawerManager : ViewGroupManager<BottomTabsDrawerView>(),
  RNBottomTabsDrawerManagerInterface<BottomTabsDrawerView> {
  private val delegate = RNBottomTabsDrawerManagerDelegate(this)
  override fun getName() = "RNBottomTabsDrawer"
  override fun getDelegate(): ViewManagerDelegate<BottomTabsDrawerView> = delegate

  override fun createViewInstance(context: ThemedReactContext): BottomTabsDrawerView {
    val view = BottomTabsDrawerView(context)
    view.onDrawerOpenChange = { open ->
      UIManagerHelper.getEventDispatcherForReactTag(context, view.id)?.dispatchEvent(
        DrawerOpenChangeEvent(UIManagerHelper.getSurfaceId(context), view.id, open),
      )
    }
    return view
  }

  override fun addView(parent: BottomTabsDrawerView, child: View, index: Int) = parent.addReactChild(child, index)
  override fun getChildCount(parent: BottomTabsDrawerView) = parent.getReactChildCount()
  override fun getChildAt(parent: BottomTabsDrawerView, index: Int) = parent.getReactChild(index)
  override fun removeViewAt(parent: BottomTabsDrawerView, index: Int) = parent.removeReactChild(index)
  override fun removeAllViews(parent: BottomTabsDrawerView) {
    while (parent.getReactChildCount() > 0) parent.removeReactChild(parent.getReactChildCount() - 1)
  }
  override fun needsCustomLayoutForChildren() = true
  override fun onDropViewInstance(view: BottomTabsDrawerView) {
    view.onDrawerOpenChange = null
    view.dispose()
    super.onDropViewInstance(view)
  }

  override fun setEnabled(view: BottomTabsDrawerView, value: Boolean) = view.setDrawerEnabled(value)
  override fun setOpen(view: BottomTabsDrawerView, value: Boolean) = view.setDrawerOpen(value)
  override fun setSwipeEnabled(view: BottomTabsDrawerView, value: Boolean) { view.swipeEnabled = value }
  override fun setHapticFeedbackEnabled(view: BottomTabsDrawerView, value: Boolean) { view.drawerHapticFeedbackEnabled = value }
  override fun setBottomBarHeight(view: BottomTabsDrawerView, value: Double) { view.bottomBarHeight = value.toFloat() }
}

private class DrawerOpenChangeEvent(surfaceId: Int, tag: Int, private val open: Boolean) :
  Event<DrawerOpenChangeEvent>(surfaceId, tag) {
  override fun getEventName() = "onDrawerOpenChange"
  override fun canCoalesce() = false
  override fun getEventData() = Arguments.createMap().apply { putBoolean("open", open) }
}

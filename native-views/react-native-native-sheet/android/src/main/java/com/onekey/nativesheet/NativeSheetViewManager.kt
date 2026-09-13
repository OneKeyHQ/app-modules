package com.onekey.nativesheet

import android.view.View
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.viewmanagers.RNCNativeSheetManagerDelegate
import com.facebook.react.viewmanagers.RNCNativeSheetManagerInterface

@ReactModule(name = NativeSheetViewManager.NAME)
class NativeSheetViewManager(
  context: ReactApplicationContext,
) : ViewGroupManager<NativeSheetView>(), RNCNativeSheetManagerInterface<NativeSheetView> {
  private val delegate = RNCNativeSheetManagerDelegate<NativeSheetView, NativeSheetViewManager>(this)

  override fun getName(): String = NAME

  override fun getDelegate(): ViewManagerDelegate<NativeSheetView> = delegate

  override fun createViewInstance(context: ThemedReactContext): NativeSheetView {
    val view = NativeSheetView(context)
    view.onDismiss = { reason ->
      val dispatcher = UIManagerHelper.getEventDispatcherForReactTag(context, view.id)
      val surfaceId = UIManagerHelper.getSurfaceId(context)
      dispatcher?.dispatchEvent(NativeSheetDismissEvent(surfaceId, view.id, reason))
    }
    view.onPresented = { height ->
      val dispatcher = UIManagerHelper.getEventDispatcherForReactTag(context, view.id)
      val surfaceId = UIManagerHelper.getSurfaceId(context)
      dispatcher?.dispatchEvent(NativeSheetPresentedEvent(surfaceId, view.id, height))
    }
    return view
  }

  override fun addEventEmitters(context: ThemedReactContext, view: NativeSheetView) {
    view.eventDispatcher = UIManagerHelper.getEventDispatcher(context)
  }

  override fun onAfterUpdateTransaction(view: NativeSheetView) {
    super.onAfterUpdateTransaction(view)
    view.commitConfiguration()
  }

  override fun onDropViewInstance(view: NativeSheetView) {
    view.onDropViewInstance()
    super.onDropViewInstance(view)
  }

  override fun addView(parent: NativeSheetView, child: View, index: Int) {
    parent.addReactChild(child, index)
  }

  override fun getChildCount(parent: NativeSheetView): Int = parent.getReactChildCount()

  override fun getChildAt(parent: NativeSheetView, index: Int): View? =
    parent.getReactChildAt(index)

  override fun removeView(parent: NativeSheetView, view: View) {
    parent.removeReactChild(view)
  }

  override fun removeAllViews(parent: NativeSheetView) {
    parent.removeAllReactChildren()
  }

  override fun removeViewAt(parent: NativeSheetView, index: Int) {
    parent.getReactChildAt(index)?.let(parent::removeReactChild)
  }

  override fun needsCustomLayoutForChildren(): Boolean = true

  override fun setOpen(view: NativeSheetView?, value: Boolean) {
    view?.open = value
  }

  override fun setSheetHeight(view: NativeSheetView?, value: Double) {
    view?.sheetHeight = value
  }

  override fun setSecurityBlocked(view: NativeSheetView?, value: Boolean) {
    view?.securityBlocked = value
  }

  override fun setDismissOnPanDown(view: NativeSheetView?, value: Boolean) {
    view?.dismissOnPanDown = value
  }

  override fun setDismissOnBackdropPress(view: NativeSheetView?, value: Boolean) {
    view?.dismissOnBackdropPress = value
  }

  override fun setDismissOnBackPress(view: NativeSheetView?, value: Boolean) {
    view?.dismissOnBackPress = value
  }

  override fun setShowHandle(view: NativeSheetView?, value: Boolean) {
    view?.showHandle = value
  }

  override fun setCornerRadius(view: NativeSheetView?, value: Double) {
    view?.cornerRadius = value
  }

  override fun setDimAmount(view: NativeSheetView?, value: Double) {
    view?.dimAmount = value
  }

  override fun setSheetBackgroundColor(view: NativeSheetView?, value: Int?) {
    view?.sheetBackgroundColor = value
  }

  companion object {
    const val NAME = "RNCNativeSheet"
  }
}

package com.onekey.nativeoverlay

import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.viewmanagers.RNCNativeOverlayToasterManagerDelegate
import com.facebook.react.viewmanagers.RNCNativeOverlayToasterManagerInterface

/** No React children: the toast cards are drawn natively from the props. */
@ReactModule(name = NativeOverlayToasterViewManager.NAME)
class NativeOverlayToasterViewManager :
  SimpleViewManager<NativeOverlayToasterView>(),
  RNCNativeOverlayToasterManagerInterface<NativeOverlayToasterView> {
  private val delegate =
    RNCNativeOverlayToasterManagerDelegate<NativeOverlayToasterView, NativeOverlayToasterViewManager>(this)

  override fun getName(): String = NAME

  override fun getDelegate(): ViewManagerDelegate<NativeOverlayToasterView> = delegate

  override fun createViewInstance(context: ThemedReactContext): NativeOverlayToasterView {
    val view = NativeOverlayToasterView(context)
    view.onToastEvent = { id, type, value ->
      UIManagerHelper.getEventDispatcherForReactTag(context, view.id)?.dispatchEvent(
        NativeOverlayToastEvent(UIManagerHelper.getSurfaceId(context), view.id, id, type, value),
      )
    }
    return view
  }

  override fun onAfterUpdateTransaction(view: NativeOverlayToasterView) {
    super.onAfterUpdateTransaction(view)
    view.commit()
  }

  override fun onDropViewInstance(view: NativeOverlayToasterView) {
    view.invalidateHost()
    super.onDropViewInstance(view)
  }

  override fun setToasts(view: NativeOverlayToasterView, value: String?) {
    view.setToasts(value)
  }

  override fun setConfig(view: NativeOverlayToasterView, value: String?) {
    view.setConfig(value)
  }

  companion object {
    const val NAME = "RNCNativeOverlayToaster"
  }
}

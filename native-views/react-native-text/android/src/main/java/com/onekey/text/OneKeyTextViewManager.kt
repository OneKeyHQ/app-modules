package com.onekey.text

import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.ReactStylesDiffMap
import com.facebook.react.uimanager.StateWrapper
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.views.text.ReactTextView
import com.facebook.react.views.text.ReactTextViewManager

@ReactModule(name = OneKeyTextViewManager.REACT_CLASS)
class OneKeyTextViewManager : ReactTextViewManager() {
  override fun getName(): String = REACT_CLASS

  override fun createViewInstance(context: ThemedReactContext): ReactTextView =
    OneKeyTextView(context)

  override fun updateState(
    view: ReactTextView,
    props: ReactStylesDiffMap,
    stateWrapper: StateWrapper,
  ): Any? {
    val stateMapBuffer = stateWrapper.stateDataMapBuffer
    // Fabric preallocation sends a default state for custom component names.
    // Real Paragraph state, including empty text, always contains serialized keys.
    if (stateMapBuffer != null && stateMapBuffer.count == 0) {
      return null
    }
    return super.updateState(view, props, stateWrapper)
  }

  companion object {
    const val REACT_CLASS = "OneKeyText"
  }
}

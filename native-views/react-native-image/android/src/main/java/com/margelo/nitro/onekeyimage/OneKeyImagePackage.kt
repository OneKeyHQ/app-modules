package com.margelo.nitro.onekeyimage

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfoProvider
import com.facebook.react.uimanager.ViewManager
import com.margelo.nitro.onekeyimage.views.HybridOneKeyImageManager

class OneKeyImagePackage : BaseReactPackage() {
  override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? = null

  override fun getReactModuleInfoProvider(): ReactModuleInfoProvider =
    ReactModuleInfoProvider { HashMap() }

  override fun createViewManagers(
    reactContext: ReactApplicationContext,
  ): List<ViewManager<*, *>> {
    OneKeyImageGlideRegistry.ensureRegistered(reactContext)
    return listOf(HybridOneKeyImageManager())
  }

  companion object {
    init {
      System.loadLibrary("onekeyimage")
    }
  }
}

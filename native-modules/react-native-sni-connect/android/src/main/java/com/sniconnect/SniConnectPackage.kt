package com.sniconnect

import com.facebook.react.TurboReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider
import com.facebook.react.uimanager.ViewManager

class SniConnectPackage : TurboReactPackage() {
  override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? =
    if (name == SniConnectModule.NAME) SniConnectModule(reactContext) else null

  override fun getReactModuleInfoProvider(): ReactModuleInfoProvider =
    ReactModuleInfoProvider {
      mapOf(
        SniConnectModule.NAME to ReactModuleInfo(
          SniConnectModule.NAME,
          SniConnectModule.NAME,
          false,
          false,
          true,
          false,
          true
        )
      )
    }

  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> =
    emptyList()
}

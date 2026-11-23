package com.margelo.nitro.reactnativejpush

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider
import cn.jiguang.plugins.core.JCoreModule
import cn.jiguang.plugins.push.JPushModule

class ReactNativeJpushPackage : BaseReactPackage() {
  override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
    JPushModule.registerReactContext(reactContext)
    JCoreModule.reactContext = reactContext
    return when (name) {
      "JPushModule" -> JPushModule(reactContext)
      "JCoreModule" -> JCoreModule(reactContext)
      else -> null
    }
  }

  override fun getReactModuleInfoProvider(): ReactModuleInfoProvider {
    return ReactModuleInfoProvider {
      val map = HashMap<String, ReactModuleInfo>()
      map["JPushModule"] = ReactModuleInfo(
        "JPushModule",
        JPushModule::class.java.name,
        true,
        true,
        false,
        false
      )
      map["JCoreModule"] = ReactModuleInfo(
        "JCoreModule",
        JCoreModule::class.java.name,
        true,
        true,
        false,
        false
      )
      map
    }
  }

  companion object {
    init {
      System.loadLibrary("reactnativejpush")
    }
  }
}

package com.margelo.nitro.reactnativedeviceutils

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise

@DoNotStrip
class ReactNativeDeviceUtils : HybridReactNativeDeviceUtilsSpec() {
  override fun hello(params: ReactNativeDeviceUtilsParams): Promise<ReactNativeDeviceUtilsResult> {
    val result = ReactNativeDeviceUtilsResult(success = true, data = "Hello, ${params.message}!")
    return Promise.resolved(result)
  }
}

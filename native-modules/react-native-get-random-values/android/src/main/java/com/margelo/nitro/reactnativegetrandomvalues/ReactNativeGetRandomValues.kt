package com.margelo.nitro.reactnativegetrandomvalues

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise

@DoNotStrip
class ReactNativeGetRandomValues : HybridReactNativeGetRandomValuesSpec() {
  override fun hello(params: ReactNativeGetRandomValuesParams): Promise<ReactNativeGetRandomValuesResult> {
    val result = ReactNativeGetRandomValuesResult(success = true, data = "Hello, ${params.message}!")
    return Promise.resolved(result)
  }
}

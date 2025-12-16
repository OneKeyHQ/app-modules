package com.margelo.nitro.reactnativegetrandomvalues

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise

@DoNotStrip
class ReactNativeGetRandomValues : HybridReactNativeGetRandomValuesSpec() {
  override fun getRandomBase64(byteLength: Double): String {
    val data = ByteArray(byteLength.toInt())
    val random = java.security.SecureRandom()

    random.nextBytes(data)

    return android.util.Base64.encodeToString(data, android.util.Base64.NO_WRAP)
  }
}
package com.margelo.nitro.reactnativegetrandomvalues

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.nativelogger.OneKeyLog

@DoNotStrip
class ReactNativeGetRandomValues : HybridReactNativeGetRandomValuesSpec() {
  companion object {
    private const val MAX_BYTE_LENGTH = 65536  // 64 KB upper bound
  }

  override fun getRandomBase64(byteLength: Double): String {
    val length = byteLength.toInt()
    if (length <= 0 || length > MAX_BYTE_LENGTH) {
      throw IllegalArgumentException("Invalid byteLength: must be 1...$MAX_BYTE_LENGTH")
    }
    try {
      val data = ByteArray(length)
      java.security.SecureRandom().nextBytes(data)
      return android.util.Base64.encodeToString(data, android.util.Base64.NO_WRAP)
    } catch (e: Exception) {
      OneKeyLog.error("RandomValues", "SecureRandom failed")
      throw e
    }
  }
}
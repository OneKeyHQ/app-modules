package com.margelo.nitro.nativelist

import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import java.nio.ByteBuffer

// PNG lifecycle tests do not load the Android-only AVIF JNI library on the JVM.
@Implements(className = "org.aomedia.avif.android.AvifDecoder", isInAndroidSdk = false)
class PngOnlyAvifDecoder {
  companion object {
    @JvmStatic
    @Implementation
    fun __staticInitializer__() = Unit

    @JvmStatic
    @Implementation
    fun isAvifImage(data: ByteBuffer): Boolean {
      check(data.limit() >= 4 && data.getInt(0) == 0x89504E47.toInt()) { "Only PNG fixtures are supported" }
      return false
    }
  }
}

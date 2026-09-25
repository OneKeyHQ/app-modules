package com.margelo.nitro.nativelogger

// Host-JVM test fixture: keep the production crypto core and BouncyCastle real
// while avoiding the production logger's Android/Nitro JNI initialization.
object OneKeyLog {
  @JvmStatic
  fun error(tag: String, message: String) = Unit
  @JvmStatic
  fun info(tag: String, message: String) = Unit
}

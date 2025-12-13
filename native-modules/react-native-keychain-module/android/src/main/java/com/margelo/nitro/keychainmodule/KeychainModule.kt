package com.margelo.nitro.keychainmodule
  
import com.facebook.proguard.annotations.DoNotStrip

@DoNotStrip
class KeychainModule : HybridKeychainModuleSpec() {
  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }
}

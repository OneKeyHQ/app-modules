package com.margelo.nitro.cloudkitmodule
  
import com.facebook.proguard.annotations.DoNotStrip

@DoNotStrip
class CloudKitModule : HybridCloudKitModuleSpec() {
  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }
}

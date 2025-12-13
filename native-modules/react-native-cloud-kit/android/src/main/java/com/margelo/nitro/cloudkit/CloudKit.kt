package com.margelo.nitro.cloudkit
  
import com.facebook.proguard.annotations.DoNotStrip

@DoNotStrip
class CloudKit : HybridCloudKitSpec() {
  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }
}

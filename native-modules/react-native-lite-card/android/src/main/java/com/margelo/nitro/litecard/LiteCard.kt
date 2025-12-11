package com.margelo.nitro.litecard
  
import com.facebook.proguard.annotations.DoNotStrip

@DoNotStrip
class LiteCard : HybridLiteCardSpec() {
  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }
}

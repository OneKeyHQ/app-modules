package com.margelo.nitro.litecard

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise

@DoNotStrip  
class LiteCard : HybridLiteCardSpec() {
  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }

  override fun checkNFCPermission(): Promise<Boolean> {
    return Promise.async { resolve, reject ->
      try {
        // TODO: Implement Android NFC permission check
        // For now, returning false as placeholder
        resolve(false)
      } catch (e: Exception) {
        reject(e)
      }
    }
  }

  override fun getLiteInfo(): Promise<Map<String, Any?>> {
    return Promise.async { resolve, reject ->
      try {
        // TODO: Implement Android getLiteInfo
        // For now, returning empty map as placeholder
        resolve(emptyMap())
      } catch (e: Exception) {
        reject(e)
      }
    }
  }

  override fun setMnemonic(mnemonic: String, pin: String, overwrite: Boolean): Promise<Boolean> {
    return Promise.async { resolve, reject ->
      try {
        // TODO: Implement Android setMnemonic
        // For now, returning false as placeholder
        resolve(false)
      } catch (e: Exception) {
        reject(e)
      }
    }
  }

  override fun getMnemonicWithPin(pin: String): Promise<String> {
    return Promise.async { resolve, reject ->
      try {
        // TODO: Implement Android getMnemonicWithPin
        // For now, returning empty string as placeholder
        resolve("")
      } catch (e: Exception) {
        reject(e)
      }
    }
  }

  override fun changePin(oldPin: String, newPin: String): Promise<Boolean> {
    return Promise.async { resolve, reject ->
      try {
        // TODO: Implement Android changePin
        // For now, returning false as placeholder
        resolve(false)
      } catch (e: Exception) {
        reject(e)
      }
    }
  }

  override fun reset(): Promise<Boolean> {
    return Promise.async { resolve, reject ->
      try {
        // TODO: Implement Android reset
        // For now, returning false as placeholder
        resolve(false)
      } catch (e: Exception) {
        reject(e)
      }
    }
  }
}

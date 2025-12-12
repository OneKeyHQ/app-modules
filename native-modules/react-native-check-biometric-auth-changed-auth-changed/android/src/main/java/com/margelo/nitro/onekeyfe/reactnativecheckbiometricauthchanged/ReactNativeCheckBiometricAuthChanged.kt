package com.margelo.nitro.onekeyfe.reactnativecheckbiometricauthchanged

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise

@DoNotStrip
class ReactNativeCheckBiometricAuthChanged : HybridReactNativeCheckBiometricAuthChangedSpec() {

  override fun checkChanged(): Promise<Boolean> {
    return Promise.resolved(false)
  }
}

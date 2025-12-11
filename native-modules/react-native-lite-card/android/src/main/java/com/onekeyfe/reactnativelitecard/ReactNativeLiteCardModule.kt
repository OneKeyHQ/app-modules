package com.onekeyfe.reactnativelitecard

import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = ReactNativeLiteCardModule.NAME)
class ReactNativeLiteCardModule(reactContext: ReactApplicationContext) :
  NativeReactNativeLiteCardSpec(reactContext) {

  override fun getName(): String {
    return NAME
  }

  override fun getLiteInfo(callback: Callback?) {
    TODO("Not yet implemented")
  }

  override fun checkNFCPermission(callback: Callback?) {
    TODO("Not yet implemented")
  }

  override fun setMnemonic(
    mnemonic: String?,
    pwd: String?,
    overwrite: Boolean,
    callback: Callback?
  ) {
    TODO("Not yet implemented")
  }

  override fun getMnemonicWithPin(
    pwd: String?,
    callback: Callback?
  ) {
    TODO("Not yet implemented")
  }

  override fun changePin(
    oldPin: String?,
    newPin: String?,
    callback: Callback?
  ) {
    TODO("Not yet implemented")
  }

  override fun reset(callback: Callback?) {
    TODO("Not yet implemented")
  }

  override fun cancel() {
    TODO("Not yet implemented")
  }

  override fun intoSetting() {
    TODO("Not yet implemented")
  }

  companion object {
    const val NAME = "ReactNativeLiteCard"
  }
}

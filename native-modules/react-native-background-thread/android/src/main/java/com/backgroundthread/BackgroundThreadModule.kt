package com.backgroundthread

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = BackgroundThreadModule.NAME)
class BackgroundThreadModule(reactContext: ReactApplicationContext) :
  NativeBackgroundThreadSpec(reactContext) {

  override fun getName(): String {
    return NAME
  }

  override fun postBackgroundMessage(message: String) {
    // TODO: Implement postBackgroundMessage
  }

  override fun startBackgroundRunnerWithEntryURL(entryURL: String) {
    // TODO: Implement startBackgroundRunnerWithEntryURL
  }

  companion object {
    const val NAME = "BackgroundThread"
  }
}

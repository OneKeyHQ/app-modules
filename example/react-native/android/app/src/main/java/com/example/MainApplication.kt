package com.example

import android.app.Application
import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactHost
import com.facebook.react.ReactInstanceEventListener
import com.facebook.react.ReactNativeApplicationEntryPoint.loadReactNative
import com.facebook.react.bridge.ReactContext
import com.facebook.react.defaults.DefaultReactHost.getDefaultReactHost
import com.backgroundthread.BackgroundThreadManager
import com.margelo.nitro.nativelogger.OneKeyLog

class MainApplication : Application(), ReactApplication {

  override val reactHost: ReactHost by lazy {
    getDefaultReactHost(
      context = applicationContext,
      packageList =
        PackageList(this).packages.apply {
          // Packages that cannot be autolinked yet can be added manually here, for example:
          // add(MyReactNativePackage())
        },
    )
  }

  override fun onCreate() {
    super.onCreate()
    OneKeyLog.info("App", "Application started")
    loadReactNative(this)

    // Mirror iOS AppDelegate's hostDidStart: — install SharedBridge and start
    // background runner as soon as the main React context is ready.
    reactHost.addReactInstanceEventListener(object : ReactInstanceEventListener {
      override fun onReactContextInitialized(context: ReactContext) {
        val manager = BackgroundThreadManager.getInstance()
        val reactAppContext = context as com.facebook.react.bridge.ReactApplicationContext
        manager.installSharedBridgeInMainRuntime(reactAppContext)

        val bgURL = if (BuildConfig.DEBUG) {
          // Use the same host detection as React Native (emulator vs device)
          val host = com.facebook.react.modules.systeminfo.AndroidInfoHelpers.getServerHost(this@MainApplication, 8082)
          "http://$host/background.bundle?platform=android&dev=true&lazy=false&minify=false&inlineSourceMap=false&modulesOnly=false&runModule=true"
        } else {
          "background.bundle"
        }
        manager.startBackgroundRunnerWithEntryURL(reactAppContext, bgURL)
      }
    })
  }
}

package com.backgroundthread

import android.util.Log
import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.ReactApplication
import com.facebook.react.ReactInstanceEventListener
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.JSBundleLoader
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.UiThreadUtil
import com.facebook.react.common.annotations.UnstableReactNativeAPI
import com.facebook.react.defaults.DefaultComponentsRegistry
import com.facebook.react.defaults.DefaultReactHostDelegate
import com.facebook.react.defaults.DefaultTurboModuleManagerDelegate
import com.facebook.react.fabric.ComponentFactory
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.runtime.ReactHostImpl
import com.facebook.react.runtime.hermes.HermesInstance

@ReactModule(name = BackgroundThreadModule.NAME)
class BackgroundThreadModule(reactContext: ReactApplicationContext) :
  NativeBackgroundThreadSpec(reactContext) {

  private var bgReactHost: ReactHostImpl? = null
  @Volatile
  private var bgRuntimePtr: Long = 0
  private var isStarted = false

  companion object {
    const val NAME = "BackgroundThread"
    private const val TAG = "BackgroundThread"

    init {
      System.loadLibrary("background_thread")
    }
  }

  // JNI declarations
  private external fun nativeInstallBgBindings(runtimePtr: Long)
  private external fun nativePostToBackground(runtimePtr: Long, message: String)
  private external fun nativeInstallSharedBridge(runtimePtr: Long, isMain: Boolean)

  override fun getName(): String = NAME

  /**
   * Called from C++ (via JNI) when background JS calls postHostMessage(message).
   * Emits the message to the main JS runtime via TurboModule event emitter.
   */
  @DoNotStrip
  fun onBgMessage(message: String) {
    UiThreadUtil.runOnUiThread {
      try {
        emitOnBackgroundMessage(message)
      } catch (e: Exception) {
        Log.e(TAG, "Error emitting background message", e)
      }
    }
  }

  override fun initBackgroundThread() {
    // Install SharedBridge in the main runtime
    val context = reactApplicationContext
    context.runOnJSQueueThread {
      try {
        val ptr = context.javaScriptContextHolder?.get() ?: 0L
        if (ptr != 0L) {
          nativeInstallSharedBridge(ptr, true)
          Log.i(TAG, "SharedBridge installed in main runtime")
        } else {
          Log.w(TAG, "Main runtime pointer is 0, cannot install SharedBridge")
        }
      } catch (e: Exception) {
        Log.e(TAG, "Error installing SharedBridge in main runtime", e)
      }
    }
  }

  @OptIn(UnstableReactNativeAPI::class)
  override fun startBackgroundRunnerWithEntryURL(entryURL: String) {
    if (isStarted) {
      Log.w(TAG, "Background runner already started")
      return
    }
    isStarted = true
    Log.i(TAG, "Starting background runner with entryURL: $entryURL")

    val appContext = reactApplicationContext.applicationContext

    val bundleLoader = if (entryURL.startsWith("http")) {
      // For dev server: load bundle directly from URL
      object : JSBundleLoader() {
        override fun loadScript(delegate: com.facebook.react.bridge.JSBundleLoaderDelegate): String {
          delegate.loadScriptFromFile(entryURL, entryURL, false)
          return entryURL
        }
      }
    } else {
      JSBundleLoader.createAssetLoader(appContext, "assets://$entryURL", true)
    }

    val delegate = DefaultReactHostDelegate(
      jsMainModulePath = "background",
      jsBundleLoader = bundleLoader,
      reactPackages = emptyList(),
      jsRuntimeFactory = HermesInstance(),
      turboModuleManagerDelegateBuilder = DefaultTurboModuleManagerDelegate.Builder(),
    )

    val componentFactory = ComponentFactory()
    DefaultComponentsRegistry.register(componentFactory)

    val host = ReactHostImpl(
      appContext,
      delegate,
      componentFactory,
      true,  /* allowPackagerServerAccess */
      false, /* useDevSupport */
    )
    bgReactHost = host

    host.addReactInstanceEventListener(object : ReactInstanceEventListener {
      override fun onReactContextInitialized(context: ReactContext) {
        Log.i(TAG, "Background ReactContext initialized")
        context.runOnJSQueueThread {
          try {
            val ptr = context.javaScriptContextHolder?.get() ?: 0L
            if (ptr != 0L) {
              bgRuntimePtr = ptr
              nativeInstallBgBindings(ptr)
              nativeInstallSharedBridge(ptr, false)
              Log.i(TAG, "JSI bindings and SharedBridge installed in background runtime")
            } else {
              Log.e(TAG, "Background runtime pointer is 0")
            }
          } catch (e: Exception) {
            Log.e(TAG, "Error installing bindings in background runtime", e)
          }
        }
      }
    })

    host.start()
  }

  override fun postBackgroundMessage(message: String) {
    val ptr = bgRuntimePtr
    if (ptr == 0L) {
      Log.w(TAG, "Cannot post message: background runtime not ready")
      return
    }

    bgReactHost?.currentReactContext?.runOnJSQueueThread {
      try {
        if (bgRuntimePtr != 0L) {
          nativePostToBackground(bgRuntimePtr, message)
        }
      } catch (e: Exception) {
        Log.e(TAG, "Error posting message to background", e)
      }
    }
  }

  override fun invalidate() {
    super.invalidate()
    bgRuntimePtr = 0
    bgReactHost?.destroy("BackgroundThreadModule invalidated", null)
    bgReactHost = null
    isStarted = false
  }
}

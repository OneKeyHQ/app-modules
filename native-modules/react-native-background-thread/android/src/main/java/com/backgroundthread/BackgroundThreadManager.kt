package com.backgroundthread

import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.ReactInstanceEventListener
import com.facebook.react.bridge.JSBundleLoader
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.common.annotations.UnstableReactNativeAPI
import com.facebook.react.defaults.DefaultComponentsRegistry
import com.facebook.react.defaults.DefaultReactHostDelegate
import com.facebook.react.defaults.DefaultTurboModuleManagerDelegate
import com.facebook.react.fabric.ComponentFactory
import com.facebook.react.runtime.ReactHostImpl
import com.facebook.react.runtime.hermes.HermesInstance

/**
 * Singleton manager for the background React Native runtime.
 * Mirrors iOS BackgroundThreadManager.
 *
 * Responsibilities:
 * - Manages background ReactHostImpl lifecycle
 * - Installs SharedBridge into main and background runtimes
 * - Routes messages between runtimes via JNI/JSI
 */
class BackgroundThreadManager private constructor() {

    private var bgReactHost: ReactHostImpl? = null

    @Volatile
    private var bgRuntimePtr: Long = 0
    private var isStarted = false
    private var onMessageCallback: ((String) -> Unit)? = null

    companion object {
        private const val MODULE_NAME = "background"

        init {
            System.loadLibrary("background_thread")
        }

        @Volatile
        private var instance: BackgroundThreadManager? = null

        @JvmStatic
        fun getInstance(): BackgroundThreadManager {
            return instance ?: synchronized(this) {
                instance ?: BackgroundThreadManager().also { instance = it }
            }
        }
    }

    // ── JNI declarations ────────────────────────────────────────────────────

    private external fun nativeInstallBgBindings(runtimePtr: Long)
    private external fun nativePostToBackground(runtimePtr: Long, message: String)
    private external fun nativeInstallSharedBridge(runtimePtr: Long, isMain: Boolean)
    private external fun nativeSetupErrorHandler(runtimePtr: Long)
    private external fun nativeDestroy()

    // ── SharedBridge ────────────────────────────────────────────────────────

    /**
     * Install SharedBridge HostObject into the main (UI) runtime.
     * Call this from initBackgroundThread().
     */
    fun installSharedBridgeInMainRuntime(context: ReactApplicationContext) {
        context.runOnJSQueueThread {
            try {
                val ptr = context.javaScriptContextHolder?.get() ?: 0L
                if (ptr != 0L) {
                    nativeInstallSharedBridge(ptr, true)
                    BTLogger.info("SharedBridge installed in main runtime")
                } else {
                    BTLogger.warn("Main runtime pointer is 0, cannot install SharedBridge")
                }
            } catch (e: Exception) {
                BTLogger.error("Error installing SharedBridge in main runtime: ${e.message}")
            }
        }
    }

    // ── Background runner lifecycle ─────────────────────────────────────────

    @OptIn(UnstableReactNativeAPI::class)
    fun startBackgroundRunnerWithEntryURL(context: ReactApplicationContext, entryURL: String) {
        if (isStarted) {
            BTLogger.warn("Background runner already started")
            return
        }
        isStarted = true
        BTLogger.info("Starting background runner with entryURL: $entryURL")

        val appContext = context.applicationContext

        val bundleLoader = if (entryURL.startsWith("http")) {
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
            jsMainModulePath = MODULE_NAME,
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
                BTLogger.info("Background ReactContext initialized")
                context.runOnJSQueueThread {
                    try {
                        val ptr = context.javaScriptContextHolder?.get() ?: 0L
                        if (ptr != 0L) {
                            bgRuntimePtr = ptr
                            nativeInstallBgBindings(ptr)
                            nativeInstallSharedBridge(ptr, false)
                            nativeSetupErrorHandler(ptr)
                            BTLogger.info("JSI bindings, SharedBridge, and error handler installed in background runtime")
                        } else {
                            BTLogger.error("Background runtime pointer is 0")
                        }
                    } catch (e: Exception) {
                        BTLogger.error("Error installing bindings in background runtime: ${e.message}")
                    }
                }
            }
        })

        host.start()
    }

    // ── Messaging ───────────────────────────────────────────────────────────

    fun postBackgroundMessage(message: String) {
        val ptr = bgRuntimePtr
        if (ptr == 0L) {
            BTLogger.warn("Cannot post message: background runtime not ready")
            return
        }

        bgReactHost?.currentReactContext?.runOnJSQueueThread {
            try {
                if (bgRuntimePtr != 0L) {
                    nativePostToBackground(bgRuntimePtr, message)
                }
            } catch (e: Exception) {
                BTLogger.error("Error posting message to background: ${e.message}")
            }
        }
    }

    fun setOnMessageCallback(callback: (String) -> Unit) {
        onMessageCallback = callback
    }

    fun checkMessageCallback(): Boolean = onMessageCallback != null

    /**
     * Called from C++ (via JNI) when background JS calls postHostMessage(message).
     * Routes the message to the registered callback (which emits to the main JS runtime).
     */
    @DoNotStrip
    fun onBgMessage(message: String) {
        onMessageCallback?.invoke(message)
    }

    // ── Lifecycle ───────────────────────────────────────────────────────────

    val isBackgroundStarted: Boolean get() = isStarted

    fun destroy() {
        nativeDestroy()
        bgRuntimePtr = 0
        bgReactHost?.destroy("BackgroundThreadManager destroyed", null)
        bgReactHost = null
        isStarted = false
        onMessageCallback = null
    }
}

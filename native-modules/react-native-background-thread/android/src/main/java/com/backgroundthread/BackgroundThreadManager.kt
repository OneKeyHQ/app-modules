package com.backgroundthread

import android.net.Uri
import com.facebook.react.ReactPackage
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
import com.facebook.react.shell.MainReactPackage
import java.io.File

/**
 * Singleton manager for the background React Native runtime.
 * Mirrors iOS BackgroundThreadManager.
 *
 * Responsibilities:
 * - Manages background ReactHostImpl lifecycle
 * - Installs SharedBridge into main and background runtimes
 * - Cross-runtime communication via SharedRPC onWrite notifications
 */
class BackgroundThreadManager private constructor() {

    private var bgReactHost: ReactHostImpl? = null
    private var reactPackages: List<ReactPackage> = emptyList()

    @Volatile
    private var bgRuntimePtr: Long = 0

    @Volatile
    private var mainRuntimePtr: Long = 0
    private var mainReactContext: ReactApplicationContext? = null
    private var isStarted = false

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

    private external fun nativeInstallSharedBridge(runtimePtr: Long, isMain: Boolean)
    private external fun nativeSetupErrorHandler(runtimePtr: Long)
    private external fun nativeDestroy()
    private external fun nativeExecuteWork(runtimePtr: Long, workId: Long)

    // ── SharedBridge ────────────────────────────────────────────────────────

    /**
     * Install SharedBridge HostObject into the main (UI) runtime.
     * Call this from installSharedBridge().
     */
    fun setReactPackages(packages: List<ReactPackage>) {
        reactPackages = packages.toList()
    }

    fun installSharedBridgeInMainRuntime(context: ReactApplicationContext) {
        mainReactContext = context
        context.runOnJSQueueThread {
            try {
                val ptr = context.javaScriptContextHolder?.get() ?: 0L
                if (ptr != 0L) {
                    mainRuntimePtr = ptr
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

    private fun isRemoteBundleUrl(entryURL: String): Boolean {
        return entryURL.startsWith("http://") || entryURL.startsWith("https://")
    }

    private fun resolveLocalBundlePath(entryURL: String): String? {
        if (entryURL.startsWith("file://")) {
            return Uri.parse(entryURL).path
        }
        if (entryURL.startsWith("/")) {
            return entryURL
        }
        return null
    }

    private fun createDownloadedBundleLoader(appContext: android.content.Context, entryURL: String): JSBundleLoader {
        return object : JSBundleLoader() {
            override fun loadScript(delegate: com.facebook.react.bridge.JSBundleLoaderDelegate): String {
                val tempFile = File(appContext.cacheDir, "background.bundle")
                try {
                    java.net.URL(entryURL).openStream().use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    BTLogger.info("Background bundle downloaded to ${tempFile.absolutePath}")
                } catch (e: Exception) {
                    BTLogger.error("Failed to download background bundle: ${e.message}")
                    throw RuntimeException("Failed to download background bundle from $entryURL", e)
                }
                delegate.loadScriptFromFile(tempFile.absolutePath, entryURL, false)
                return entryURL
            }
        }
    }

    private fun createLocalFileBundleLoader(localPath: String, sourceURL: String): JSBundleLoader {
        return object : JSBundleLoader() {
            override fun loadScript(delegate: com.facebook.react.bridge.JSBundleLoaderDelegate): String {
                val bundleFile = File(localPath)
                if (!bundleFile.exists()) {
                    BTLogger.error("Background bundle file does not exist: $localPath")
                    throw RuntimeException("Background bundle file does not exist: $localPath")
                }
                delegate.loadScriptFromFile(bundleFile.absolutePath, sourceURL, false)
                return sourceURL
            }
        }
    }

    @OptIn(UnstableReactNativeAPI::class)
    fun startBackgroundRunnerWithEntryURL(context: ReactApplicationContext, entryURL: String) {
        if (isStarted) {
            BTLogger.warn("Background runner already started")
            return
        }
        BTLogger.info("Starting background runner with entryURL: $entryURL")

        val appContext = context.applicationContext
        val packages =
            if (reactPackages.isNotEmpty()) {
                reactPackages
            } else {
                BTLogger.warn("No ReactPackages registered for background runtime; call setReactPackages(...) from host before start. Falling back to MainReactPackage only.")
                listOf(MainReactPackage())
            }

        val localBundlePath = resolveLocalBundlePath(entryURL)
        val bundleLoader =
            when {
                isRemoteBundleUrl(entryURL) -> createDownloadedBundleLoader(appContext, entryURL)
                localBundlePath != null -> createLocalFileBundleLoader(localBundlePath, entryURL)
                entryURL.startsWith("assets://") -> JSBundleLoader.createAssetLoader(appContext, entryURL, true)
                else -> JSBundleLoader.createAssetLoader(appContext, "assets://$entryURL", true)
            }

        val delegate = DefaultReactHostDelegate(
            jsMainModulePath = MODULE_NAME,
            jsBundleLoader = bundleLoader,
            reactPackages = packages,
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
                            nativeInstallSharedBridge(ptr, false)
                            nativeSetupErrorHandler(ptr)
                            BTLogger.info("SharedBridge and error handler installed in background runtime")
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
        isStarted = true
    }

    /**
     * Called from C++ RuntimeExecutor to schedule work on the correct JS thread.
     * Routes to main or background runtime's JS queue thread, then calls nativeExecuteWork.
     */
    @DoNotStrip
    fun scheduleOnJSThread(isMain: Boolean, workId: Long) {
        val context = if (isMain) mainReactContext else bgReactHost?.currentReactContext
        BTLogger.info("scheduleOnJSThread: isMain=$isMain, workId=$workId, context=${context != null}")
        if (context == null) {
            BTLogger.error("scheduleOnJSThread: context is null! isMain=$isMain, mainCtx=${mainReactContext != null}, bgHost=${bgReactHost != null}, bgCtx=${bgReactHost?.currentReactContext != null}")
        }
        context?.runOnJSQueueThread {
            // Re-read ptr inside the block — if a reload happened between
            // scheduling and execution, the old ptr may be stale.
            val ptr = if (isMain) mainRuntimePtr else bgRuntimePtr
            BTLogger.info("scheduleOnJSThread runOnJSQueueThread: isMain=$isMain, workId=$workId, ptr=$ptr")
            if (ptr != 0L) {
                try {
                    nativeExecuteWork(ptr, workId)
                } catch (e: Exception) {
                    BTLogger.error("Error executing work on JS thread: ${e.message}")
                }
            } else {
                BTLogger.error("scheduleOnJSThread: ptr is 0! isMain=$isMain")
            }
        }
    }

    // ── Lifecycle ───────────────────────────────────────────────────────────

    val isBackgroundStarted: Boolean get() = isStarted

    fun destroy() {
        nativeDestroy()
        bgRuntimePtr = 0
        mainRuntimePtr = 0
        mainReactContext = null
        bgReactHost?.destroy("BackgroundThreadManager destroyed", null)
        bgReactHost = null
        isStarted = false
    }
}
